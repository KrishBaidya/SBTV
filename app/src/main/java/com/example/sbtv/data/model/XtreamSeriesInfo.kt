package com.example.sbtv.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response from Xtream API `get_series_info&series_id=X`.
 * Contains episode data grouped by season number.
 */
data class XtreamSeriesInfo(
    @SerializedName("episodes") val episodes: Map<String, List<XtreamEpisode>>?
)

data class XtreamEpisode(
    @SerializedName("id") val id: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("season") val season: Int?,
    @SerializedName("episode_num") val episodeNum: Int?,
    @SerializedName("container_extension") val containerExtension: String?
)
