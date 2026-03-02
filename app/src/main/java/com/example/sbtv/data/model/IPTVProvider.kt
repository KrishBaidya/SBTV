package com.example.sbtv.data.model

data class IPTVProvider(
    val id: String,
    val name: String,
    val type: ProviderType,
    val baseUrl: String,
    val username: String? = null,
    val password: String? = null
)