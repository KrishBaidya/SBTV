package com.example.sbtv.data.playlist

import com.example.sbtv.data.model.Channel

class M3UParser {

    fun parse(content: String): List<Channel> {

        val lines = content.lines()
        val channels = mutableListOf<Channel>()

        var name = ""
        var logo: String? = null
        var group: String? = null

        for (i in lines.indices) {

            val line = lines[i].trim()

            if (line.startsWith("#EXTINF")) {

                name = line.substringAfter(",")

                logo = line.substringAfter("tvg-logo=\"", "")
                    .substringBefore("\"", "")
                    .ifBlank { null }

                group = line.substringAfter("group-title=\"", "")
                    .substringBefore("\"", "")
                    .ifBlank { null }
            }

            // A typical M3U IPTV playlist has the stream URL on the line following #EXTINF.
            // But some might just have raw URLs, so we check for common protocols.
            if (line.startsWith("http://") || line.startsWith("https://")) {

                channels.add(
                    Channel(
                        id = line.hashCode().toString(),
                        name = name.ifBlank { "Channel ${channels.size + 1}" },
                        streamUrl = line,
                        logo = logo,
                        group = group
                    )
                )
                
                // Reset metadata for the next channel
                name = ""
                logo = null
                group = null
            }
        }

        return channels
    }
}
