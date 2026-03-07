package com.example.sbtv.ui.screens.tv

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.videolan.libvlc.MediaPlayer
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.sbtv.data.model.Channel
import com.example.sbtv.ui.theme.GoldPrimary

private const val TAG = "TVPlayerScreen"

@Composable
fun TVPlayerScreen(
    navController: NavController,
    streamUrl: String? = null,
    fromTV: Boolean = true,
    viewModel: TVViewModel = viewModel()
) {
    val context = LocalContext.current
    val channels by viewModel.channels.collectAsState()
    
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val maxVolume = remember { audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat() }
    
    var showChannelList by remember { mutableStateOf(false) }
    var isControllerVisible by remember { mutableStateOf(false) }
    
    val listState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }
    
    var userInteractionTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var volume by remember { mutableStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / maxVolume) } 
    var brightness by remember { mutableStateOf(
        (context as? Activity)?.window?.attributes?.screenBrightness?.takeIf { it >= 0 } ?: 0.5f
    ) }
    var resizeMode by remember { mutableStateOf(MediaPlayer.ScaleType.SURFACE_BEST_FIT) }

    // Track whether we've already started playback so we don't re-trigger
    var hasStartedPlayback by remember { mutableStateOf(false) }
    
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

    // Back button logic: First show channel list, if already shown, then exit
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
    ) {
        // Video Player Container
        VideoPlayer(
            viewModel = viewModel, 
            useController = !showChannelList,
            resizeMode = resizeMode,
            onControllerVisibilityChanged = { isVisible ->
                isControllerVisible = isVisible
            }
        )

        // Dim background when list is shown
        if (showChannelList) {
            
            // 8 second auto-hide timer
            LaunchedEffect(showChannelList, userInteractionTime) {
                kotlinx.coroutines.delay(8000)
                showChannelList = false
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showChannelList = false }
            )
        }

        // --- Overlays ---
        
        // Channel List Overlay (Left Sidebar)
        AnimatedVisibility(
            visible = showChannelList,
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it })
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(320.dp),
                color = Color(0xFF0B0F1A).copy(alpha = 0.95f),
                tonalElevation = 8.dp
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, tint = GoldPrimary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Channel List",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Context-Aware List Filter
                    val activeGroup by viewModel.activeGroup.collectAsState()
                    val playingChannelUri = viewModel.playerManager.currentUrl
                    val relatedChannels = remember(channels, activeGroup) {
                        if (activeGroup != null) channels.filter { it.group == activeGroup } else channels
                    }

                    // Auto-scroll logic
                    LaunchedEffect(showChannelList, relatedChannels) {
                        if (showChannelList) {
                            val activeIndex = relatedChannels.indexOfFirst { it.streamUrl == playingChannelUri }
                            if (activeIndex >= 0) {
                                listState.scrollToItem(activeIndex.coerceAtLeast(0))
                            }
                            // Small delay to ensure items are composed before requesting focus
                            kotlinx.coroutines.delay(100)
                            try { focusRequester.requestFocus() } catch (e: Exception) {}
                        }
                    }

                    // List
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(relatedChannels) { channel ->
                            val isPlaying = channel.streamUrl == playingChannelUri
                            
                            val itemModifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isPlaying) GoldPrimary.copy(alpha = 0.15f) else Color.Transparent)
                                .then(if (isPlaying) Modifier.focusRequester(focusRequester) else Modifier)
                                .clickable { 
                                    viewModel.playChannelList(channel)
                                    userInteractionTime = System.currentTimeMillis() 
                                }
                                .padding(12.dp)
                                
                            Row(
                                modifier = itemModifier,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Mini Preview / Logo
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White.copy(alpha = 0.1f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!channel.logo.isNullOrBlank()) {
                                        AsyncImage(
                                            model = channel.logo,
                                            contentDescription = null,
                                            contentScale = ContentScale.Fit,
                                            modifier = Modifier.size(36.dp)
                                        )
                                    } else {
                                        val initials = channel.name.take(2).uppercase()
                                        val gradient = Brush.linearGradient(
                                            colors = listOf(
                                                Color(channel.name.hashCode() or 0xFF000000.toInt()),
                                                Color(channel.id.hashCode() or 0xFF000000.toInt())
                                            )
                                        )
                                        Box(
                                            modifier = Modifier.fillMaxSize().background(gradient),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(initials, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = channel.name,
                                        color = if (isPlaying) GoldPrimary else Color.White,
                                        fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 15.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (isPlaying) {
                                        Text("Now Playing", color = GoldPrimary, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Controls (Right side buttons)
        AnimatedVisibility(
            visible = isControllerVisible && !showChannelList,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 32.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ControlIconButton(
                    icon = if (resizeMode == MediaPlayer.ScaleType.SURFACE_BEST_FIT) Icons.Default.AspectRatio else Icons.Default.Fullscreen,
                    label = "Aspect",
                    onClick = {
                        resizeMode = if (resizeMode == MediaPlayer.ScaleType.SURFACE_BEST_FIT) 
                            MediaPlayer.ScaleType.SURFACE_FILL 
                        else 
                            MediaPlayer.ScaleType.SURFACE_BEST_FIT
                    }
                )
                
                ControlIconButton(icon = Icons.AutoMirrored.Filled.VolumeUp, label = "Vol", onClick = { 
                    volume = (volume + 0.1f).let { if (it > 1f) 0f else it }
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (volume * maxVolume).toInt(), AudioManager.FLAG_SHOW_UI)
                })
                ControlIconButton(icon = Icons.Default.Brightness6, label = "Bright", onClick = { 
                    brightness = (brightness + 0.1f).let { if (it > 1f) 0.1f else it }
                })
            }
        }

        // Top Right/Left Quick Controls (Next / Prev)
        AnimatedVisibility(
            visible = isControllerVisible && !showChannelList,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopStart).padding(32.dp)
        ) {
            ControlIconButton(icon = Icons.Default.SkipPrevious, label = "Prev", onClick = { 
                if (viewModel.playerManager.hasPrevious()) {
                    viewModel.playerManager.previous()
                }
            })
        }
        
        AnimatedVisibility(
            visible = isControllerVisible && !showChannelList,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopEnd).padding(32.dp)
        ) {
            ControlIconButton(icon = Icons.Default.SkipNext, label = "Next", onClick = { 
                if (viewModel.playerManager.hasNext()) {
                    viewModel.playerManager.next()
                }
            })
        }

        // Feedback Text
        if (!showChannelList && fromTV && isControllerVisible) {
            Text(
                text = "Press BACK for Channel List",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 24.dp)
            )
        }
    }
}

@Composable
fun ControlIconButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier
                .size(48.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = androidx.compose.material3.ripple(color = GoldPrimary)
                ) { onClick() },
            shape = CircleShape,
            color = if (isFocused) GoldPrimary.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.3f),
            border = androidx.compose.foundation.BorderStroke(if (isFocused) 2.dp else 1.dp, if (isFocused) GoldPrimary else Color.White.copy(alpha = 0.2f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, tint = if (isFocused) Color.White else GoldPrimary, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, color = if (isFocused) GoldPrimary else Color.White, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}
