package com.example.sbtv.data.playlist

import android.util.Log
import com.example.sbtv.data.model.XtreamLiveStream
import com.example.sbtv.data.model.XtreamSeries
import com.example.sbtv.data.model.XtreamSeriesInfo
import com.example.sbtv.data.model.XtreamVodStream
import com.example.sbtv.data.model.XtreamCategory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

class XtreamFetcher {

    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()
    private val gson = Gson()

    fun login(baseUrl: String, username: String, password: String): Boolean {
        return try {
            val safeBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
            val url = "$safeBaseUrl/player_api.php?username=$username&password=$password"
            Log.d("XtreamFetcher", "Testing login: $url")
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("XtreamFetcher", "Login failed with code: ${response.code}")
                    return false
                }
                val json = response.body.string()
                Log.d("XtreamFetcher", "Login response: $json")
                // Success usually contains "auth": 1
                json.contains("\"auth\":1") || json.contains("\"auth\": 1")
            }
        } catch (e: Exception) {
            Log.e("XtreamFetcher", "Login exception", e)
            false
        }
    }

    fun fetchLiveStreams(baseUrl: String, username: String, password: String): List<XtreamLiveStream> {
        val safeBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
        val url = "$safeBaseUrl/player_api.php?username=$username&password=$password&action=get_live_streams"
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Failed to load Xtream channels: ${response.code}")
            val json = response.body.string()
            val type = object : TypeToken<List<XtreamLiveStream>>() {}.type
            return try {
                gson.fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                Log.e("XtreamFetcher", "Error parsing live streams", e)
                emptyList()
            }
        }
    }

    fun fetchVodStreams(baseUrl: String, username: String, password: String): List<XtreamVodStream> {
        val safeBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
        val url = "$safeBaseUrl/player_api.php?username=$username&password=$password&action=get_vod_streams"
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Failed to load Xtream VODs: ${response.code}")
            val json = response.body.string()
            val type = object : TypeToken<List<XtreamVodStream>>() {}.type
            return try {
                gson.fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                Log.e("XtreamFetcher", "Error parsing VOD streams", e)
                emptyList()
            }
        }
    }

    fun fetchSeriesList(baseUrl: String, username: String, password: String): List<XtreamSeries> {
        val safeBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
        val url = "$safeBaseUrl/player_api.php?username=$username&password=$password&action=get_series"
        val request = Request.Builder().url(url).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Failed to load Xtream Series: ${response.code}")
            val json = response.body.string()
            val type = object : TypeToken<List<XtreamSeries>>() {}.type
            return try {
                gson.fromJson(json, type) ?: emptyList()
            } catch (e: Exception) {
                Log.e("XtreamFetcher", "Error parsing series list", e)
                emptyList()
            }
        }
    }

    /**
     * Fetch full series info including all episodes grouped by season.
     * Calls: get_series_info&series_id=X
     */
    fun fetchSeriesInfo(baseUrl: String, username: String, password: String, seriesId: String): XtreamSeriesInfo? {
        val safeBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
        val url = "$safeBaseUrl/player_api.php?username=$username&password=$password&action=get_series_info&series_id=$seriesId"
        val request = Request.Builder().url(url).build()
        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e("XtreamFetcher", "Failed to load series info for $seriesId: ${response.code}")
                    return null
                }
                val json = response.body.string()
                gson.fromJson(json, XtreamSeriesInfo::class.java)
            }
        } catch (e: Exception) {
            Log.e("XtreamFetcher", "Error fetching series info for $seriesId", e)
            null
        }
    }

    fun fetchLiveCategories(baseUrl: String, username: String, password: String): List<XtreamCategory> {
        return fetchCategories(baseUrl, username, password, "get_live_categories")
    }

    fun fetchVodCategories(baseUrl: String, username: String, password: String): List<XtreamCategory> {
        return fetchCategories(baseUrl, username, password, "get_vod_categories")
    }

    fun fetchSeriesCategories(baseUrl: String, username: String, password: String): List<XtreamCategory> {
        return fetchCategories(baseUrl, username, password, "get_series_categories")
    }

    private fun fetchCategories(baseUrl: String, username: String, password: String, action: String): List<XtreamCategory> {
        val safeBaseUrl = if (baseUrl.endsWith("/")) baseUrl.dropLast(1) else baseUrl
        val url = "$safeBaseUrl/player_api.php?username=$username&password=$password&action=$action"
        val request = Request.Builder().url(url).build()
        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return emptyList()
                val json = response.body.string()
                val type = object : TypeToken<List<XtreamCategory>>() {}.type
                gson.fromJson(json, type) ?: emptyList()
            }
        } catch (e: Exception) {
            Log.e("XtreamFetcher", "Error parsing categories for $action", e)
            emptyList()
        }
    }
}
