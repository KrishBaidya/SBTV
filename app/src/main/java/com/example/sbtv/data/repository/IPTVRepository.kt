package com.example.sbtv.data.repository

import android.content.Context
import android.util.Log
import com.example.sbtv.data.model.Channel
import com.example.sbtv.data.model.Movie
import com.example.sbtv.data.model.Series
import com.example.sbtv.data.model.ParsedPlaylist
import com.example.sbtv.data.playlist.M3UParser
import com.example.sbtv.data.playlist.PlaylistFetcher
import com.example.sbtv.data.playlist.XtreamFetcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IPTVRepository(context: Context? = null) {

    private val parser = M3UParser()
    private val fetcher = PlaylistFetcher()
    private val xtreamFetcher = XtreamFetcher()

    // M3U cache — only available when Context is provided (always when called from ViewModel)
    private val m3uRepository: M3URepository? = context?.let { M3URepository(it) }

    suspend fun loadM3UChannels(url: String): ParsedPlaylist {
        return withContext(Dispatchers.IO) {
            // Use cache if repo is available; fall back to direct fetch otherwise
            m3uRepository?.getPlaylist(url) ?: run {
                val content = fetcher.fetch(url)
                parser.parse(content)
            }
        }
    }

    suspend fun testXtreamConnection(baseUrl: String, username: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            xtreamFetcher.login(baseUrl, username, password)
        }
    }

    suspend fun loadXtreamChannels(baseUrl: String, username: String, password: String): List<Channel> {
        return withContext(Dispatchers.IO) {
            val categories = xtreamFetcher.fetchLiveCategories(baseUrl, username, password)
            val categoryMap = categories.associate { it.categoryId to it.categoryName }

            val liveStreams = xtreamFetcher.fetchLiveStreams(baseUrl, username, password)
            val categories = xtreamFetcher.fetchLiveCategories(baseUrl, username, password)
            
            val categoryMap = categories.associateBy({ it.categoryId }, { it.categoryName })
            
            val safeBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl

            liveStreams.map { stream ->
                val groupName = categoryMap[stream.categoryId] ?: stream.categoryId ?: "Uncategorized"
                
                Channel(
                    id = stream.streamId?.toString() ?: stream.hashCode().toString(),
                    name = stream.name ?: "Unknown Channel",
                    streamUrl = "$safeBaseUrl/$username/$password/${stream.streamId}",
                    logo = stream.streamIcon,
                    group = groupName
                )
            }
        }
    }

    suspend fun loadXtreamMovies(baseUrl: String, username: String, password: String): List<Movie> {
        return withContext(Dispatchers.IO) {
            val categories = xtreamFetcher.fetchVodCategories(baseUrl, username, password)
            val categoryMap = categories.associate { it.categoryId to it.categoryName }

            val vodStreams = xtreamFetcher.fetchVodStreams(baseUrl, username, password)
            val categories = xtreamFetcher.fetchVodCategories(baseUrl, username, password)
            
            val categoryMap = categories.associateBy({ it.categoryId }, { it.categoryName })
            
            val safeBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl

            vodStreams.map { stream ->
                val extension = stream.containerExtension ?: "mp4"
                val groupName = categoryMap[stream.categoryId] ?: stream.categoryId ?: "Uncategorized"
                
                Movie(
                    id = stream.streamId?.toString() ?: stream.hashCode().toString(),
                    name = stream.name ?: "Unknown Movie",
                    streamUrl = "$safeBaseUrl/movie/$username/$password/${stream.streamId}.$extension",
                    poster = stream.streamIcon,
                    category = groupName
                )
            }
        }
    }

    /**
     * Fetch full episode list for a single Xtream series (on-demand).
     * Calls get_series_info API to get episodes with playable stream URLs.
     */
    suspend fun loadXtreamSeriesEpisodes(
        baseUrl: String, username: String, password: String, seriesId: String,
        seriesName: String, poster: String?, category: String?
    ): List<Series> {
        return withContext(Dispatchers.IO) {
            val safeBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl

            try {
                val seriesInfo = xtreamFetcher.fetchSeriesInfo(baseUrl, username, password, seriesId)
                val episodesBySeason = seriesInfo?.episodes

                if (!episodesBySeason.isNullOrEmpty()) {
                    val episodes = mutableListOf<Series>()
                    for ((seasonKey, seasonEpisodes) in episodesBySeason) {
                        for (ep in seasonEpisodes) {
                            val epId = ep.id ?: continue
                            val extension = ep.containerExtension ?: "mp4"
                            val seasonNum = ep.season ?: seasonKey.toIntOrNull() ?: 1
                            val episodeNum = ep.episodeNum ?: 0

                            episodes.add(
                                Series(
                                    id = "${seriesId}_${seasonNum}_${episodeNum}",
                                    name = ep.title ?: "$seriesName S${seasonNum.toString().padStart(2, '0')}E${episodeNum.toString().padStart(2, '0')}",
                                    streamUrl = "$safeBaseUrl/series/$username/$password/$epId.$extension",
                                    poster = poster,
                                    category = category,
                                    seriesName = seriesName,
                                    season = "S${seasonNum.toString().padStart(2, '0')}",
                                    episodeNum = "E${episodeNum.toString().padStart(2, '0')}"
                                )
                            )
                        }
                    }
                    episodes
                } else {
                    Log.w("IPTVRepository", "No episodes found for series: $seriesName ($seriesId)")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("IPTVRepository", "Error fetching series info for $seriesName ($seriesId)", e)
                emptyList()
            }
        }
    }
}

