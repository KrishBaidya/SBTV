package com.example.sbtv.data.model

data class Series(
    val id: String,
    val name: String,
    val poster: String? = null,
    val category: String? = null
)