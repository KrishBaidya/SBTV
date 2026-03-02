package com.example.sbtv

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory

class PlayerManager(context: Context) {

    val player: ExoPlayer = ExoPlayer.Builder(context)
        .setMediaSourceFactory(DefaultMediaSourceFactory(context))
        .build()

    private var currentUrl: String? = null

    fun play(url: String) {
        if (currentUrl == url) {
            if (!player.isPlaying) player.play()
            return
        }
        
        currentUrl = url
        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMimeType(if (url.contains("m3u8")) MimeTypes.APPLICATION_M3U8 else null)
            .build()
            
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = true
    }

    fun stop() {
        currentUrl = null
        player.stop()
    }

    fun release() {
        player.release()
    }
}
