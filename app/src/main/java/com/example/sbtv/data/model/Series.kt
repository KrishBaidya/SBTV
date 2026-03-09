package com.example.sbtv.data.model

data class Series(
    val id: String,
    val name: String,
    val streamUrl: String = "",
    val poster: String? = null,
    val category: String? = null,
    val seriesName: String = "",
    val season: String? = null,
    val episodeNum: String? = null
)