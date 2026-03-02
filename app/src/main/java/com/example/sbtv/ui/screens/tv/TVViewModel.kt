package com.example.sbtv.ui.screens.tv

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sbtv.PlayerManager
import com.example.sbtv.data.local.PlaybackDataStore
import com.example.sbtv.data.local.ProviderDataStore
import com.example.sbtv.data.model.Channel
import com.example.sbtv.data.model.IPTVProvider
import com.example.sbtv.data.model.ProviderType
import com.example.sbtv.data.repository.IPTVRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collectLatest

class TVViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = IPTVRepository()
    private val playbackStore = PlaybackDataStore(application)

    val playerManager = PlayerManager(application)

    private val _channels = MutableStateFlow<List<Channel>>(emptyList())
    val channels: StateFlow<List<Channel>> = _channels

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val providerStore = ProviderDataStore(application)

    init {
        loadFromSavedProvider()
    }

    private fun loadFromSavedProvider() {
        viewModelScope.launch {
            _isLoading.value = true
            
            providerStore.getProviders().collectLatest { providers ->
                val provider = providers.firstOrNull()
                if (provider != null) {
                    try {
                        when (provider.type) {
                            ProviderType.M3U -> {
                                var result = repository.loadM3UChannels(provider.baseUrl)
                                
                                if (result.isEmpty()) {
                                    result = listOf(
                                        Channel(
                                            id = provider.baseUrl.hashCode().toString(),
                                            name = provider.name.ifBlank { "Test Stream" },
                                            streamUrl = provider.baseUrl,
                                            logo = null,
                                            group = null
                                        )
                                    )
                                }
                                
                                _channels.value = result
                            }
                            ProviderType.XTREAM -> {
                                val result = repository.loadXtreamChannels(
                                    baseUrl = provider.baseUrl,
                                    username = provider.username ?: "",
                                    password = provider.password ?: ""
                                )
                                _channels.value = result
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        val fallbackChannel = listOf(
                            Channel(
                                id = provider.baseUrl.hashCode().toString(),
                                name = provider.name.ifBlank { "Test Stream" },
                                streamUrl = provider.baseUrl,
                                logo = null,
                                group = null
                            )
                        )
                        _channels.value = fallbackChannel
                    } finally {
                        _isLoading.value = false
                    }
                } else {
                    _isLoading.value = false
                    _channels.value = emptyList()
                }
            }
        }
    }

    fun playLastOrDefault() {
        viewModelScope.launch {
            val currentChannels = _channels.value
            if (currentChannels.isEmpty()) return@launch

            val lastUrl = playbackStore.getLastChannel().firstOrNull()
            val channelToPlay = currentChannels.find { it.streamUrl == lastUrl }
                ?: currentChannels.firstOrNull()

            channelToPlay?.let {
                playerManager.play(it.streamUrl)
            }
        }
    }

    fun playChannel(channel: Channel) {
        viewModelScope.launch {
            playbackStore.saveLastChannel(channel.streamUrl)
        }
        playerManager.play(channel.streamUrl)
    }

    fun stopPlayback() {
        playerManager.stop()
    }

    override fun onCleared() {
        super.onCleared()
        playerManager.release()
    }
}
