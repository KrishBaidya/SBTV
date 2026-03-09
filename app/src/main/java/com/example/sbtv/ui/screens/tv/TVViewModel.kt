package com.example.sbtv.ui.screens.tv

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sbtv.PlayerManager
import com.example.sbtv.data.local.PlaybackDataStore
import com.example.sbtv.data.local.ProviderDataStore
import com.example.sbtv.data.model.Channel
import com.example.sbtv.data.model.IPTVProvider
import com.example.sbtv.data.model.ProviderType
import com.example.sbtv.data.repository.IPTVRepository
import com.example.sbtv.data.model.Movie
import com.example.sbtv.data.model.Series
import com.example.sbtv.data.model.SeriesGroup
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.asStateFlow
import com.example.sbtv.data.local.CustomCategoryDataStore
import com.example.sbtv.data.local.ChannelHealthStore
import com.example.sbtv.data.model.CustomCategory

private const val TAG = "TVViewModel"

class TVViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = IPTVRepository(application)
    private val playbackStore = PlaybackDataStore(application)
    private val providerStore = ProviderDataStore(application)
    private val customCategoryStore = CustomCategoryDataStore(application)
    private val healthStore = ChannelHealthStore(application)
    
    val playerManager = PlayerManager(application, healthStore)

    private val _rawChannels = MutableStateFlow<List<Channel>>(emptyList())
    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _series = MutableStateFlow<List<Series>>(emptyList())
    val series: StateFlow<List<Series>> = _series.asStateFlow()

    private val _seriesGroups = MutableStateFlow<List<SeriesGroup>>(emptyList())
    val seriesGroups: StateFlow<List<SeriesGroup>> = _seriesGroups.asStateFlow()

    private val _selectedSeriesGroup = MutableStateFlow<SeriesGroup?>(null)
    val selectedSeriesGroup: StateFlow<SeriesGroup?> = _selectedSeriesGroup.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _isLoadingEpisodes = MutableStateFlow(false)
    val isLoadingEpisodes: StateFlow<Boolean> = _isLoadingEpisodes.asStateFlow()
    
    private val _activeGroup = MutableStateFlow<String?>(null)
    val activeGroup: StateFlow<String?> = _activeGroup.asStateFlow()
    
    // Store active provider credentials for on-demand series episode fetching
    private var activeProviderBaseUrl: String = ""
    private var activeProviderUsername: String = ""
    private var activeProviderPassword: String = ""
    private var activeProviderType: ProviderType = ProviderType.M3U
    
    val customCategories = customCategoryStore.getCategories()

    init {
        loadFromSavedProvider()
        
        // Listen and apply custom categories + failed stream logic
        viewModelScope.launch {
            combine(
                _rawChannels, 
                customCategoryStore.getCategories(), 
                healthStore.getFailedStreams()
            ) { raw, customCats, failedStreams ->
                val modifiedRaw = mutableListOf<Channel>()
                
                // Add all original channels
                modifiedRaw.addAll(raw)
                
                // Add exact duplicates of channels that are mapped to custom categories
                customCats.forEach { cat ->
                    val matchingChannels = raw.filter { cat.channelIds.contains(it.id) }
                    modifiedRaw.addAll(matchingChannels.map { it.copy(group = cat.name) })
                }
                
                // Sort the complete list. Put failed streams at the absolute bottom
                modifiedRaw.sortedBy { if (failedStreams.contains(it.streamUrl)) 1 else 0 }
            }.collectLatest { computed ->
                _channels.value = computed
            }
        }
    }

    private fun loadFromSavedProvider() {
        viewModelScope.launch {
            _isLoading.value = true
            
                
            combine(providerStore.getProviders(), providerStore.getActiveProviderId()) { providers, activeId ->
                providers to activeId
            }.collectLatest { (providers, activeId) ->
                val provider = if (activeId != null) {
                    providers.find { it.id == activeId } ?: providers.firstOrNull()
                } else {
                    providers.firstOrNull()
                }
                
                if (provider != null) {
                    try {
                        when (provider.type) {
                            ProviderType.M3U -> {
                                val parsedPlaylist = repository.loadM3UChannels(provider.baseUrl)
                                
                                if (parsedPlaylist.channels.isEmpty() && parsedPlaylist.movies.isEmpty() && parsedPlaylist.series.isEmpty()) {
                                    Log.w(TAG, "M3U parse returned empty — treating URL as direct stream")
                                    _rawChannels.value = listOf(
                                        Channel(
                                            id = provider.baseUrl.hashCode().toString(),
                                            name = provider.name.ifBlank { "Test Stream" },
                                            streamUrl = provider.baseUrl,
                                            logo = null,
                                            group = null
                                        )
                                    )
                                } else {
                                    Log.d(TAG, "M3U loaded: ${parsedPlaylist.channels.size} channels, ${parsedPlaylist.movies.size} movies, ${parsedPlaylist.series.size} series")
                                    _rawChannels.value = parsedPlaylist.channels
                                    _movies.value = parsedPlaylist.movies
                                    _series.value = parsedPlaylist.series
                                    _seriesGroups.value = groupSeries(parsedPlaylist.series)
                                }
                            }
                            ProviderType.XTREAM -> {
                                // Store credentials for on-demand series episode fetching
                                activeProviderBaseUrl = provider.baseUrl
                                activeProviderUsername = provider.username ?: ""
                                activeProviderPassword = provider.password ?: ""
                                activeProviderType = ProviderType.XTREAM
                                
                                val channels = repository.loadXtreamChannels(
                                    baseUrl = provider.baseUrl,
                                    username = provider.username ?: "",
                                    password = provider.password ?: ""
                                )
                                _rawChannels.value = channels
                                
                                val movies = repository.loadXtreamMovies(
                                    baseUrl = provider.baseUrl,
                                    username = provider.username ?: "",
                                    password = provider.password ?: ""
                                )
                                _movies.value = movies
                                
                                val seriesList = repository.loadXtreamSeries(
                                    baseUrl = provider.baseUrl,
                                    username = provider.username ?: "",
                                    password = provider.password ?: ""
                                )
                                _series.value = seriesList
                                _seriesGroups.value = groupXtreamSeries(seriesList)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to load provider data", e)
                        e.printStackTrace()
                        _rawChannels.value = listOf(
                            Channel(
                                id = provider.baseUrl.hashCode().toString(),
                                name = provider.name.ifBlank { "Test Stream (Error)" },
                                streamUrl = provider.baseUrl,
                                logo = null,
                                group = null
                            )
                        )
                    } finally {
                        _isLoading.value = false
                    }
                } else {
                    _isLoading.value = false
                    _rawChannels.value = emptyList()
                    _movies.value = emptyList()
                    _series.value = emptyList()
                }
            }
        }
    }

    fun playLastOrDefault() {
        viewModelScope.launch {
            val currentChannels = _channels.value
            if (currentChannels.isEmpty()) {
                Log.w(TAG, "playLastOrDefault: channels list is empty — cannot play")
                return@launch
            }

            val lastUrl = playbackStore.getLastChannel().firstOrNull()
            val channelToPlay = currentChannels.find { it.streamUrl == lastUrl }
                ?: currentChannels.firstOrNull()

            channelToPlay?.let {
                Log.d(TAG, "playLastOrDefault: playing '${it.name}' — ${it.streamUrl}")
                playChannelList(it)
            }
        }
    }

    fun playChannelList(channel: Channel) {
        viewModelScope.launch {
            val currentChannels = _channels.value
            val sameGroupChannels = currentChannels.filter { it.group == channel.group }
            val urls = sameGroupChannels.map { it.streamUrl }
            
            _activeGroup.value = channel.group
            val startIndex = urls.indexOf(channel.streamUrl).coerceAtLeast(0)
            
            Log.d(TAG, "playChannelList: '${channel.name}' group='${channel.group}' index=$startIndex/${urls.size}")
            
            playbackStore.saveLastChannel(channel.streamUrl)
            playerManager.playList(urls, startIndex)
        }
    }

    fun playChannel(channel: Channel) {
        playChannelList(channel)
    }

    fun stopPlayback() {
        playerManager.stop()
    }

    // ── Series Playback ─────────────────────────────────────────────────────

    fun selectSeriesGroup(group: SeriesGroup) {
        _selectedSeriesGroup.value = group
    }

    /**
     * Load episodes on-demand for an Xtream series group.
     * Called when user enters the series player screen.
     */
    fun loadSeriesEpisodes(group: SeriesGroup) {
        if (group.episodesLoaded) return // Already loaded
        if (group.xtreamSeriesId == null) return // Not an Xtream series (M3U already has episodes)
        
        viewModelScope.launch {
            _isLoadingEpisodes.value = true
            try {
                val episodes = repository.loadXtreamSeriesEpisodes(
                    baseUrl = activeProviderBaseUrl,
                    username = activeProviderUsername,
                    password = activeProviderPassword,
                    seriesId = group.xtreamSeriesId,
                    seriesName = group.name,
                    poster = group.poster,
                    category = group.category
                )
                
                Log.d(TAG, "loadSeriesEpisodes: '${group.name}' loaded ${episodes.size} episodes")
                
                // Update the group with loaded episodes
                val updatedGroup = group.copy(
                    episodes = episodes.sortedWith(
                        compareBy<Series> { it.season ?: "" }
                            .thenBy { it.episodeNum ?: "" }
                            .thenBy { it.name }
                    ),
                    episodesLoaded = true
                )
                
                // Replace the group in the list
                _seriesGroups.value = _seriesGroups.value.map {
                    if (it.id == group.id) updatedGroup else it
                }
                _selectedSeriesGroup.value = updatedGroup
            } catch (e: Exception) {
                Log.e(TAG, "Error loading episodes for '${group.name}'", e)
            } finally {
                _isLoadingEpisodes.value = false
            }
        }
    }

    fun playSeriesEpisode(episode: Series, allEpisodes: List<Series>) {
        viewModelScope.launch {
            // Only include episodes with valid, non-blank stream URLs
            val validEpisodes = allEpisodes.filter { it.streamUrl.isNotBlank() }
            if (validEpisodes.isEmpty()) {
                Log.w(TAG, "playSeriesEpisode: No valid episodes to play")
                return@launch
            }
            val urls = validEpisodes.map { it.streamUrl }
            val startIndex = urls.indexOf(episode.streamUrl).coerceAtLeast(0)
            Log.d(TAG, "playSeriesEpisode: '${episode.name}' index=$startIndex/${urls.size}")
            playerManager.playList(urls, startIndex)
        }
    }

    /**
     * Group M3U series (which already have playable URLs in each episode).
     */
    private fun groupSeries(seriesList: List<Series>): List<SeriesGroup> {
        val validSeries = seriesList.filter { it.streamUrl.isNotBlank() }
        return validSeries.groupBy { 
            it.seriesName.ifBlank { it.name }
        }.map { (groupName, episodes) ->
            val first = episodes.first()
            SeriesGroup(
                id = groupName.hashCode().toString(),
                name = groupName,
                poster = first.poster,
                category = first.category,
                episodes = episodes.sortedWith(
                    compareBy<Series> { it.season ?: "" }
                        .thenBy { it.episodeNum ?: "" }
                        .thenBy { it.name }
                ),
                episodesLoaded = true // M3U episodes are already loaded
            )
        }.sortedBy { it.name }
    }

    /**
     * Group Xtream series metadata into SeriesGroups (no episodes yet - loaded on-demand).
     * Each entry is one show with xtreamSeriesId for later fetching.
     */
    private fun groupXtreamSeries(seriesList: List<Series>): List<SeriesGroup> {
        return seriesList.map { series ->
            SeriesGroup(
                id = series.id,
                name = series.seriesName.ifBlank { series.name },
                poster = series.poster,
                category = series.category,
                episodes = emptyList(), // Loaded on-demand
                xtreamSeriesId = series.id,
                episodesLoaded = false
            )
        }.sortedBy { it.name }
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }

    fun addCustomCategory(name: String) {
        viewModelScope.launch {
            val current = customCategoryStore.getCategories().firstOrNull() ?: emptyList()
            val newCategory = CustomCategory(
                id = java.util.UUID.randomUUID().toString(),
                name = name,
                channelIds = emptyList()
            )
            customCategoryStore.saveCategories(current + newCategory)
        }
    }

    fun renameCustomCategory(categoryId: String, newName: String) {
        viewModelScope.launch {
            val current = customCategoryStore.getCategories().firstOrNull() ?: emptyList()
            val updated = current.map { if (it.id == categoryId) it.copy(name = newName) else it }
            customCategoryStore.saveCategories(updated)
        }
    }

    fun deleteCustomCategory(categoryId: String) {
        viewModelScope.launch {
            val current = customCategoryStore.getCategories().firstOrNull() ?: emptyList()
            val updated = current.filter { it.id != categoryId }
            customCategoryStore.saveCategories(updated)
        }
    }

    fun toggleChannelInCustomCategory(categoryId: String, channelId: String) {
        viewModelScope.launch {
            val current = customCategoryStore.getCategories().firstOrNull() ?: emptyList()
            val updated = current.map { cat ->
                if (cat.id == categoryId) {
                    val channels = cat.channelIds.toMutableList()
                    if (channels.contains(channelId)) {
                        channels.remove(channelId)
                    } else {
                        channels.add(channelId)
                    }
                    cat.copy(channelIds = channels)
                } else cat
            }
            customCategoryStore.saveCategories(updated)
        }
    }
}
