package com.example.sbtv.ui.screens.tv

import android.util.Log
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.videolan.libvlc.MediaPlayer
import org.videolan.libvlc.util.VLCVideoLayout
import android.view.View

private const val TAG = "VideoPlayer"

/**
 * Renders the active video player surface using VLC.
 */
@Composable
fun VideoPlayer(
    viewModel: TVViewModel,
    useController: Boolean = false, // Not natively used by VLCVideoLayout, we manage controls in TVPlayerScreen
    resizeMode: MediaPlayer.ScaleType = MediaPlayer.ScaleType.SURFACE_BEST_FIT,
    onControllerVisibilityChanged: (Boolean) -> Unit = {}
) {
    Log.d(TAG, "Rendering surface for engine: VLC")
    val vlcPlayer = viewModel.playerManager.vlcPlayer

    AndroidView(
        factory = { ctx ->
            VLCVideoLayout(ctx).also { vlcLayout ->
                try {
                    vlcPlayer.attachViews(vlcLayout, null, true, false)
                    vlcPlayer.videoScale = resizeMode
                    
                    // Trigger custom controller overlay on click if wanted. 
                    // VLC layout click doesn't give a built-in controller, so we simulate an event for TVPlayerScreen
                    vlcLayout.setOnClickListener {
                        // Assuming TVPlayerScreen manages its own visibility when tapping the screen Box, 
                        // so we might not need to do anything here since Box overlaps it.
                        // But if event is consumed, let's trigger:
                        onControllerVisibilityChanged(true)
                    }
                    Log.d(TAG, "VLC views attached successfully")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to attach VLC views", e)
                }
            }
        },
        update = { vlcLayout ->
            vlcPlayer.videoScale = resizeMode
        },
        modifier = Modifier.fillMaxSize()
    )

    DisposableEffect(Unit) {
        onDispose {
            try {
                vlcPlayer.detachViews()
                Log.d(TAG, "VLC views detached")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to detach VLC views", e)
            }
        }
    }
}
