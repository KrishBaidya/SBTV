package com.example.sbtv.ui.screens.tv

import android.view.KeyEvent
import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(
    viewModel: TVViewModel,
    useController: Boolean = false,
    onUserInteraction: () -> Unit = {}
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }

    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = viewModel.playerManager.player
                this.useController = useController
                
                // Keep screen on while playing video
                keepScreenOn = true
                
                // Hide system UI (fullscreen)
                controllerHideOnTouch = true
            }
        },
        update = { vlcLayout ->
            vlcPlayer.videoScale = resizeMode
        },
        modifier = Modifier
            .fillMaxSize()
            .clickable(interactionSource = interactionSource, indication = null) {
                onUserInteraction()
            }
            .onKeyEvent {
                // Register D-pad interactions for Android TV
                if (it.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                    onUserInteraction()
                }
                false
            }
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
