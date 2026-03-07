package com.example.sbtv.data.repository

import android.content.Context
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
            val safeBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl

            liveStreams.map { stream ->
                Channel(
                    id = stream.streamId?.toString() ?: stream.hashCode().toString(),
                    name = stream.name ?: "Unknown Channel",
                    streamUrl = "$safeBaseUrl/$username/$password/${stream.streamId}",
                    logo = stream.streamIcon,
                    group = categoryMap[stream.categoryId] ?: stream.categoryId
                )
            }
        }
    }

    suspend fun loadXtreamMovies(baseUrl: String, username: String, password: String): List<Movie> {
        return withContext(Dispatchers.IO) {
            val categories = xtreamFetcher.fetchVodCategories(baseUrl, username, password)
            val categoryMap = categories.associate { it.categoryId to it.categoryName }

            val vodStreams = xtreamFetcher.fetchVodStreams(baseUrl, username, password)
            val safeBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl

            vodStreams.map { stream ->
                val extension = stream.containerExtension ?: "mp4"
                Movie(
                    id = stream.streamId?.toString() ?: stream.hashCode().toString(),
                    name = stream.name ?: "Unknown Movie",
                    streamUrl = "$safeBaseUrl/movie/$username/$password/${stream.streamId}.$extension",
                    poster = stream.streamIcon,
                    category = categoryMap[stream.categoryId] ?: stream.categoryId
                )
            }
        }
    }

    suspend fun loadXtreamSeries(baseUrl: String, username: String, password: String): List<Series> {
        return withContext(Dispatchers.IO) {
            val categories = xtreamFetcher.fetchSeriesCategories(baseUrl, username, password)
            val categoryMap = categories.associate { it.categoryId to it.categoryName }

            val seriesStreams = xtreamFetcher.fetchSeriesList(baseUrl, username, password)
            val safeBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl

            seriesStreams.map { stream ->
                Series(
                    id = stream.seriesId ?: stream.hashCode().toString(),
                    name = stream.name ?: "Unknown Series",
                    poster = stream.cover,
                    category = categoryMap[stream.categoryId] ?: stream.categoryId
                )
            }
        }
    }
}
