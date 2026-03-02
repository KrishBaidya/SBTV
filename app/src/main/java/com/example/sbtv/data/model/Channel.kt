package com.example.sbtv.data.model

data class Channel(
    val id: String,
    val name: String,
    val streamUrl: String,
    val logo: String? = null,
    val group: String? = null
)