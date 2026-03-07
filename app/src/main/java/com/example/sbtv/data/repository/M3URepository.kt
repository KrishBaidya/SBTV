package com.example.sbtv.data.repository

import android.content.Context
import android.util.Log
import com.example.sbtv.data.model.ParsedPlaylist
import com.example.sbtv.data.playlist.M3UParser
import com.example.sbtv.data.playlist.PlaylistFetcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private const val TAG = "M3URepository"

/**
 * Caches the raw M3U playlist content to disk for 30 minutes.
 * On a cache hit, parsing happens from the local file (no network call).
 * On a miss or expired cache, the playlist is fetched, saved, and then parsed.
 *
 * All operations run on [Dispatchers.IO] — safe to call from any coroutine scope.
 */
class M3URepository(context: Context) {

    private val cacheFile = File(context.cacheDir, "playlist_cache.m3u")
    private val parser = M3UParser()
    private val fetcher = PlaylistFetcher()

    companion object {
        /** 30 minutes in milliseconds */
        private const val CACHE_DURATION_MS = 30 * 60 * 1000L
    }

    suspend fun getPlaylist(url: String): ParsedPlaylist = withContext(Dispatchers.IO) {
        if (isCacheValid()) {
            Log.d(TAG, "Cache hit — parsing from disk")
            try {
                return@withContext parser.parse(cacheFile.readText())
            } catch (e: Exception) {
                Log.w(TAG, "Cache read failed, fetching from network", e)
            }
        }

        Log.d(TAG, "Cache miss — fetching from network: $url")
        val content = fetcher.fetch(url)

        // Persist to disk for next launch
        try {
            cacheFile.writeText(content)
            Log.d(TAG, "Playlist cached successfully (${content.length} chars)")
        } catch (e: Exception) {
            Log.w(TAG, "Failed to write cache file", e)
        }

        parser.parse(content)
    }

    /** Returns true when the cache file exists and is younger than [CACHE_DURATION_MS]. */
    private fun isCacheValid(): Boolean =
        cacheFile.exists() &&
            (System.currentTimeMillis() - cacheFile.lastModified()) < CACHE_DURATION_MS
}
