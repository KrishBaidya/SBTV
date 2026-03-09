package com.example.sbtv.data.model

data class ParsedPlaylist(
    val channels: List<Channel> = emptyList(),
    val movies: List<Movie> = emptyList(),
    val series: List<Series> = emptyList()
)
