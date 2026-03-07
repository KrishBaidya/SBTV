package com.example.sbtv.data.playlist

import android.util.Log
import com.example.sbtv.data.model.Channel
import com.example.sbtv.data.model.Movie
import com.example.sbtv.data.model.Series
import com.example.sbtv.data.model.ParsedPlaylist

class M3UParser {

    fun parse(content: String): ParsedPlaylist {
        Log.d("M3UParser", "Starting parse, content length: ${content.length}")
        val lines = content.lines()
        val channels = mutableListOf<Channel>()
        val movies = mutableListOf<Movie>()
        val series = mutableListOf<Series>()

        var name = ""
        var logo: String? = null
        var group: String? = null

        for (i in lines.indices) {
            val line = lines[i].trim()
            if (line.isBlank()) continue

            if (line.startsWith("#EXTINF")) {
                // Better name parsing: take everything after the last comma
                name = line.substringAfterLast(",").trim()

                logo = line.substringAfter("tvg-logo=\"", "")
                    .substringBefore("\"", "")
                    .ifBlank { null }

                group = line.substringAfter("group-title=\"", "")
                    .substringBefore("\"", "")
                    .ifBlank { null }
                
                Log.d("M3UParser", "Parsed INF: name=$name, group=$group")
            } else if (!line.startsWith("#")) {
                // This should be the URL
                val url = line
                val currentTitle = name.ifBlank { url.substringAfterLast("/").substringBefore("?") }
                // Use original group title but standardise empty ones to "Uncategorized"
                var currentGroup = group?.trim()?.ifBlank { "Uncategorized" } ?: "Uncategorized"
                
                // Identify kids channels
                val lowerGroup = currentGroup.lowercase()
                val lowerTitle = currentTitle.lowercase()
                if (lowerGroup.contains("kids") || 
                    lowerGroup.contains("cartoon") || 
                    lowerGroup.contains("animation") ||
                    lowerTitle.contains("kids") ||
                    lowerTitle.contains("cartoon") ||
                    lowerTitle.contains("pogo") ||
                    lowerTitle.contains("disney") ||
                    lowerTitle.contains("nickelodeon") ||
                    lowerTitle.contains("nick jr")) {
                    currentGroup = "***KIDS***"
                }
                
                Log.d("M3UParser", "Processing URL: $url for $currentTitle in $currentGroup")

                when {
                    isSeries(currentGroup, url) -> {
                        series.add(
                            Series(
                                id = url.hashCode().toString(),
                                name = currentTitle,
                                poster = logo,
                                category = group
                            )
                        )
                    }
                    isMovie(currentGroup, url) -> {
                        movies.add(
                            Movie(
                                id = url.hashCode().toString(),
                                name = currentTitle,
                                streamUrl = url,
                                poster = logo,
                                category = group
                            )
                        )
                    }
                    else -> {
                        channels.add(
                            Channel(
                                id = url.hashCode().toString(),
                                name = currentTitle,
                                streamUrl = url,
                                logo = logo,
                                group = group
                            )
                        )
                    }
                }
                
                // Reset metadata for the next item
                name = ""
                logo = null
                group = null
            }
        }

        Log.d("M3UParser", "Parse complete: ${channels.size} channels, ${movies.size} movies, ${series.size} series")
        return ParsedPlaylist(channels, movies, series)
    }

    private fun isMovie(group: String, url: String): Boolean {
        val lowerGroup = group.lowercase()
        return lowerGroup.contains("movie") || 
               lowerGroup.contains("vod") || 
               lowerGroup.contains("cinema") || 
               lowerGroup.contains("film") ||
               url.contains("/movie/") ||
               url.endsWith(".mp4") ||
               url.endsWith(".mkv") ||
               url.endsWith(".avi")
    }

    private fun isSeries(group: String, url: String): Boolean {
        val lowerGroup = group.lowercase()
        return lowerGroup.contains("series") || 
               lowerGroup.contains("show") || 
               lowerGroup.contains("episode") || 
               lowerGroup.contains("season") ||
               url.contains("/series/")
    }
}
