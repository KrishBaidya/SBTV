package com.example.sbtv.data.playlist

import com.example.sbtv.data.model.XtreamCategory
import com.example.sbtv.data.model.XtreamLiveStream
import com.example.sbtv.data.model.XtreamVodStream
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request

class XtreamFetcher {

    private val client = OkHttpClient()
    private val gson = Gson()

    fun fetchLiveCategories(baseUrl: String, username: String, password: String): List<XtreamCategory> {
        val safeBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
        val url = "$safeBaseUrl/player_api.php?username=$username&password=$password&action=get_live_categories"

        val request = Request.Builder()
            .url(url)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return emptyList()
                val json = response.body.string()
                val type = object : TypeToken<List<XtreamCategory>>() {}.type
                gson.fromJson(json, type) ?: emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun fetchVodCategories(baseUrl: String, username: String, password: String): List<XtreamCategory> {
        val safeBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
        val url = "$safeBaseUrl/player_api.php?username=$username&password=$password&action=get_vod_categories"

        val request = Request.Builder()
            .url(url)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return emptyList()
                val json = response.body.string()
                val type = object : TypeToken<List<XtreamCategory>>() {}.type
                gson.fromJson(json, type) ?: emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun fetchLiveStreams(baseUrl: String, username: String, password: String): List<XtreamLiveStream> {
        val safeBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
        val url = "$safeBaseUrl/player_api.php?username=$username&password=$password&action=get_live_streams"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Failed to load Xtream channels")
            }
            
            val json = response.body.string()
            val type = object : TypeToken<List<XtreamLiveStream>>() {}.type
            return gson.fromJson(json, type) ?: emptyList()
        }
    }

    fun fetchVodStreams(baseUrl: String, username: String, password: String): List<XtreamVodStream> {
        val safeBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
        val url = "$safeBaseUrl/player_api.php?username=$username&password=$password&action=get_vod_streams"

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                throw Exception("Failed to load Xtream VODs")
            }

            val json = response.body.string()
            val type = object : TypeToken<List<XtreamVodStream>>() {}.type
            return gson.fromJson(json, type) ?: emptyList()
        }
    }
}
