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
                        val parsed = parseSeriesInfo(currentTitle)
                        series.add(
                            Series(
                                id = url.hashCode().toString(),
                                name = currentTitle,
                                streamUrl = url,
                                poster = logo,
                                category = group,
                                seriesName = parsed.seriesName,
                                season = parsed.season,
                                episodeNum = parsed.episodeNum
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

    /**
     * Extract series name, season, and episode from titles like:
     * "Breaking Bad S01 E01", "The Office S02E05", "Narcos Season 3 Episode 10"
     */
    private fun parseSeriesInfo(title: String): SeriesParseResult {
        // Pattern: S01E02, S01 E02, s1e2
        val sXeX = Regex("""(.+?)\s*[Ss](\d+)\s*[Ee](\d+)""")
        sXeX.find(title)?.let { match ->
            return SeriesParseResult(
                seriesName = match.groupValues[1].trim(),
                season = "S${match.groupValues[2].padStart(2, '0')}",
                episodeNum = "E${match.groupValues[3].padStart(2, '0')}"
            )
        }

        // Pattern: Season 1 Episode 2
        val seasonEp = Regex("""(.+?)\s*Season\s*(\d+)\s*Episode\s*(\d+)""", RegexOption.IGNORE_CASE)
        seasonEp.find(title)?.let { match ->
            return SeriesParseResult(
                seriesName = match.groupValues[1].trim(),
                season = "S${match.groupValues[2].padStart(2, '0')}",
                episodeNum = "E${match.groupValues[3].padStart(2, '0')}"
            )
        }

        // Pattern: S01 only (no episode) 
        val sOnly = Regex("""(.+?)\s*[Ss](\d+)""")
        sOnly.find(title)?.let { match ->
            return SeriesParseResult(
                seriesName = match.groupValues[1].trim(),
                season = "S${match.groupValues[2].padStart(2, '0')}",
                episodeNum = null
            )
        }

        // Pattern: Episode N or Ep N or EP N
        val epOnly = Regex("""(.+?)\s*(?:Episode|Ep|EP)\s*(\d+)""", RegexOption.IGNORE_CASE)
        epOnly.find(title)?.let { match ->
            return SeriesParseResult(
                seriesName = match.groupValues[1].trim(),
                season = "S01",
                episodeNum = "E${match.groupValues[2].padStart(2, '0')}"
            )
        }

        // Fallback: use entire title as series name
        return SeriesParseResult(seriesName = title, season = "S01", episodeNum = null)
    }

    private data class SeriesParseResult(
        val seriesName: String,
        val season: String?,
        val episodeNum: String?
    )

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
