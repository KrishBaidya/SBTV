package com.example.sbtv

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.sbtv.data.local.ChannelHealthStore
import com.example.sbtv.player.PlayerPreferenceManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer

private const val TAG = "PlayerManager"

/**
 * Unified player manager using VLC completely.
 * Use [switchChannel] for single-URL playback, [playList] for playlist mode.
 */
class PlayerManager(
    private val context: Context,
    val healthStore: ChannelHealthStore? = null,
    private val prefManager: PlayerPreferenceManager = PlayerPreferenceManager(context)
) {

    // ── VLC ───────────────────────────────────────────────────────────────────

    private val vlcOptions = arrayListOf(
        "--network-caching=3000",
        "--rtsp-tcp",
        "-vvv"
    )

    @Volatile
    private var vlcInitialized = false

    val libVLC: LibVLC by lazy {
        vlcInitialized = true
        LibVLC(context, vlcOptions)
    }
    
    val vlcPlayer: MediaPlayer by lazy { 
        val player = MediaPlayer(libVLC) 
        player.setEventListener(vlcEventListener)
        player
    }

    private var currentPlaylist: List<String> = emptyList()
    private var currentIndex: Int = -1
    var currentUrl: String? = null
        private set

    private val vlcEventListener = MediaPlayer.EventListener { event ->
        when (event.type) {
            MediaPlayer.Event.EncounteredError -> {
                Log.e(TAG, "VLC playback error on URL: $currentUrl")
                currentUrl?.let { failedUrl ->
                    healthStore?.let {
                        CoroutineScope(Dispatchers.IO).launch {
                            it.addFailedStream(failedUrl)
                        }
                    }
                }
                
                // Auto skip
                if (hasNext()) {
                    Log.d(TAG, "Skipping to next stream after error")
                    next()
                } else {
                    Log.w(TAG, "No next stream available — playback stopped after error")
                }
            }
            MediaPlayer.Event.EndReached -> {
                 if (hasNext()) {
                    Log.d(TAG, "End reached, skipping to next stream automatically")
                    next()
                 }
            }
        }
    }

    // ── Public API ───────────────────────────────────────────────────────────

    val selectedPlayer: String
        get() = PlayerPreferenceManager.PLAYER_VLC

    fun switchChannel(url: String): Boolean {
        if (url.isBlank()) {
            return false
        }
        currentPlaylist = listOf(url)
        currentIndex = 0
        currentUrl = url
        return switchVLC(url)
    }

    fun play(url: String) = switchChannel(url)

    fun playList(urls: List<String>, startIndex: Int = 0) {
        val validUrls = urls.filter { it.isNotBlank() }
        if (validUrls.isEmpty()) {
            return
        }

        currentPlaylist = validUrls
        currentIndex = startIndex.coerceIn(0, validUrls.lastIndex)
        currentUrl = currentPlaylist[currentIndex]
        switchVLC(currentUrl!!)
    }
    
    fun hasNext(): Boolean {
        return currentPlaylist.isNotEmpty() && currentIndex < currentPlaylist.lastIndex
    }
    
    fun hasPrevious(): Boolean {
        return currentPlaylist.isNotEmpty() && currentIndex > 0
    }
    
    fun next(): Boolean {
        if (hasNext()) {
            currentIndex++
            currentUrl = currentPlaylist[currentIndex]
            return switchVLC(currentUrl!!)
        }
        return false
    }
    
    fun previous(): Boolean {
        if (hasPrevious()) {
            currentIndex--
            currentUrl = currentPlaylist[currentIndex]
            return switchVLC(currentUrl!!)
        }
        return false
    }

    fun stop() {
        if (vlcInitialized) {
            try {
                vlcPlayer.stop()
            } catch (e: Exception) {
                Log.e(TAG, "VLC stop error", e)
            }
        }
    }

    fun release() {
        if (vlcInitialized) {
            try { vlcPlayer.release() } catch (e: Exception) { Log.e(TAG, "VLC release error", e) }
            try { libVLC.release() } catch (e: Exception) { Log.e(TAG, "LibVLC release error", e) }
        }
    }

    // ── Private ──────────────────────────────────────────────────────────────

    private fun switchVLC(url: String): Boolean {
        Log.d(TAG, "switchVLC: $url")
        return try {
            vlcPlayer.stop()
            val media = Media(libVLC, Uri.parse(url))
            media.setHWDecoderEnabled(true, false)
            vlcPlayer.media = media
            media.release()
            vlcPlayer.play()
            true
        } catch (e: Exception) {
            Log.e(TAG, "VLC playback failed: $url", e)
            false
        }
    }
}
