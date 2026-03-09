package com.example.sbtv.data.repository

import com.example.sbtv.data.model.Channel
import com.example.sbtv.data.model.Movie
import com.example.sbtv.data.playlist.M3UParser
import com.example.sbtv.data.playlist.PlaylistFetcher
import com.example.sbtv.data.playlist.XtreamFetcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IPTVRepository {

    private val parser = M3UParser()
    private val fetcher = PlaylistFetcher()
    private val xtreamFetcher = XtreamFetcher()

    suspend fun loadM3UChannels(url: String): List<Channel> {

        return withContext(Dispatchers.IO) {

            val content = fetcher.fetch(url)
            parser.parse(content)
        }
    }

    suspend fun loadXtreamChannels(baseUrl: String, username: String, password: String): List<Channel> {
        return withContext(Dispatchers.IO) {
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
}
