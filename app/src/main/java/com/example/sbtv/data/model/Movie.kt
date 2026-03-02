package com.example.sbtv.data.model

data class Movie(
    val id: String,
    val name: String,
    val streamUrl: String,
    val poster: String? = null,
    val category: String? = null
)