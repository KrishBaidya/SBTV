package com.example.sbtv.data.model

data class CustomCategory(
    val id: String,
    val name: String,
    val channelIds: List<String> = emptyList()
)
