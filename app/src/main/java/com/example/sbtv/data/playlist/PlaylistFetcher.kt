package com.example.sbtv.data.playlist

import okhttp3.OkHttpClient
import okhttp3.Request

class PlaylistFetcher {

    private val client = OkHttpClient()

    fun fetch(url: String): String {

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Failed to load playlist")
            }
            return response.body?.string() ?: ""
        }
    }
}