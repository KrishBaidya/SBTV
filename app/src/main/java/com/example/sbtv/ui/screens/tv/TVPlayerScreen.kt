package com.example.sbtv.ui.screens.tv

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import org.videolan.libvlc.MediaPlayer
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun TVPlayerScreen(
    navController: NavController,
    streamUrl: String? = null,
    fromTV: Boolean = true,
    viewModel: TVViewModel = viewModel()
) {
    val context = LocalContext.current
    val channels by viewModel.channels.collectAsState()
    var isOverlayVisible by remember { mutableStateOf(true) }
    
    val context = LocalContext.current
    val view = LocalView.current

    // Immersive Fullscreen Mode
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        if (window != null) {
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
        }

        onDispose {
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    // Track whether we've already started playback so we don't re-trigger
    var hasStartedPlayback by remember { mutableStateOf(false) }
    
    // Auto-hide controls after 5 seconds
    LaunchedEffect(controlInteractionTime) {
        if (showControls) {
            kotlinx.coroutines.delay(5000)
            showControls = false
        }
    }
    
    // Update brightness when it changes
    LaunchedEffect(brightness) {
        val activity = context as? Activity
        activity?.window?.attributes = activity?.window?.attributes?.apply {
            screenBrightness = brightness
        }
    }
    
    // ─── THE CORE PLAYBACK TRIGGER ──────────────────────────────────────────
    // Key on BOTH streamUrl AND channels so that:
    //   1. If streamUrl is provided → play it immediately (single channel/movie)
    //   2. If streamUrl is null → wait until channels are loaded, then auto-play
    // hasStartedPlayback prevents re-triggering when channel list updates later
    LaunchedEffect(streamUrl, channels) {
        if (hasStartedPlayback) return@LaunchedEffect

        if (!streamUrl.isNullOrBlank()) {
            Log.d(TAG, "Playing direct URL: $streamUrl")
            viewModel.playerManager.switchChannel(streamUrl)
            hasStartedPlayback = true
        } else if (channels.isNotEmpty()) {
            Log.d(TAG, "No URL provided — playing last or first channel (${channels.size} available)")
            viewModel.playLastOrDefault()
            hasStartedPlayback = true
        } else {
            Log.d(TAG, "Waiting for channels to load...")
        }
    }

    // Auto-hide overlay after 3 seconds
    LaunchedEffect(isOverlayVisible) {
        if (isOverlayVisible) {
            delay(3000)
            isOverlayVisible = false
        }
    }

    BackHandler {
        if (fromTV) {
            if (showChannelList) {
                viewModel.stopPlayback()
                navController.popBackStack()
            } else {
                showChannelList = true
            }
        } else {
            viewModel.stopPlayback()
            navController.popBackStack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                userInteractionTime = System.currentTimeMillis()
            }
            .onKeyEvent { keyEvent ->
                if (keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                    // Show controls on any D-pad key press
                    if (!showChannelList) {
                        showControls = true
                        controlInteractionTime = System.currentTimeMillis()
                    }
                    
                    when (keyEvent.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                            if (showChannelList) {
                                false // Let the list handle selection
                            } else {
                                viewModel.playerManager.togglePlayPause()
                                true
                            }
                        }
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            if (!showChannelList) {
                                viewModel.playerManager.next()
                                true
                            } else false
                        }
                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            if (!showChannelList) {
                                viewModel.playerManager.previous()
                                true
                            } else false
                        }
                        else -> false
                    }
                } else false
            }
    ) {
        VideoPlayer(
            viewModel = viewModel,
            useController = true,
            onUserInteraction = { isOverlayVisible = true }
        )

        AnimatedVisibility(
            visible = isOverlayVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)
                        )
                    )
                    .padding(vertical = 32.dp, horizontal = 24.dp)
            ) {
                Text(
                    text = if (streamUrl != null) "Press Back for Library" else "Press Back for Live TV Channels",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}

