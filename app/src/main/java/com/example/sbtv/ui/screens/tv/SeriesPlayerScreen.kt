package com.example.sbtv.ui.screens.tv

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.util.Log
import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.videolan.libvlc.MediaPlayer
import androidx.navigation.NavController
import com.example.sbtv.data.model.Series
import com.example.sbtv.ui.theme.GoldPrimary

private const val TAG = "SeriesPlayerScreen"

@Composable
fun SeriesPlayerScreen(
    navController: NavController,
    seriesGroupId: String,
    viewModel: TVViewModel = viewModel()
) {
    val context = LocalContext.current
    val seriesGroups by viewModel.seriesGroups.collectAsState()
    val isLoadingEpisodes by viewModel.isLoadingEpisodes.collectAsState()
    
    // Find the series group for this screen (reactive — updates when episodes load)
    val seriesGroup = remember(seriesGroups, seriesGroupId) {
        seriesGroups.find { it.id == seriesGroupId }
    }

    // Trigger on-demand episode loading for Xtream series
    LaunchedEffect(seriesGroup) {
        if (seriesGroup != null && !seriesGroup.episodesLoaded) {
            Log.d(TAG, "Triggering on-demand episode load for '${seriesGroup.name}'")
            viewModel.loadSeriesEpisodes(seriesGroup)
        }
    }

    // Show loading while group not found or episodes not loaded
    if (seriesGroup == null || (!seriesGroup.episodesLoaded && isLoadingEpisodes)) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = GoldPrimary)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Loading episodes...",
                    color = Color.White,
                    fontSize = 16.sp
                )
                if (seriesGroup != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        seriesGroup.name,
                        color = GoldPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Back handler while loading
        BackHandler {
            viewModel.stopPlayback()
            navController.popBackStack()
        }
        return
    }

    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    val maxVolume = remember { audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat() }

    var showEpisodeList by remember { mutableStateOf(false) }
    var isControllerVisible by remember { mutableStateOf(false) }
    
    val listState = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }
    
    var userInteractionTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var volume by remember { mutableStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) / maxVolume) }
    var brightness by remember { mutableStateOf(
        (context as? Activity)?.window?.attributes?.screenBrightness?.takeIf { it >= 0 } ?: 0.5f
    ) }
    var resizeMode by remember { mutableStateOf(MediaPlayer.ScaleType.SURFACE_BEST_FIT) }
    var hasStartedPlayback by remember { mutableStateOf(false) }

    // Season selection
    val allSeasons = remember(seriesGroup) { seriesGroup.seasons.ifEmpty { listOf("S01") } }
    var selectedSeason by remember { mutableStateOf(allSeasons.firstOrNull() ?: "S01") }
    
    val episodesForSeason = remember(seriesGroup, selectedSeason) {
        val filtered = seriesGroup.episodesForSeason(selectedSeason)
        if (filtered.isEmpty()) seriesGroup.episodes else filtered
    }
    
    // Only use episodes with valid stream URLs
    val allEpisodes = remember(seriesGroup) {
        seriesGroup.episodes.filter { it.streamUrl.isNotBlank() }
    }
    val hasPlayableContent = allEpisodes.isNotEmpty()

    // Update brightness
    LaunchedEffect(brightness) {
        val activity = context as? Activity
        activity?.window?.attributes = activity?.window?.attributes?.apply {
            screenBrightness = brightness
        }
    }

    // Auto-play first episode once episodes are loaded
    LaunchedEffect(seriesGroup.episodesLoaded, allEpisodes) {
        if (!hasStartedPlayback && seriesGroup.episodesLoaded && allEpisodes.isNotEmpty()) {
            val firstEpisode = allEpisodes.first()
            Log.d(TAG, "Auto-playing first episode of '${seriesGroup.name}': ${firstEpisode.name}")
            viewModel.playSeriesEpisode(firstEpisode, allEpisodes)
            hasStartedPlayback = true
        }
    }

    // Back button: first show episode list, second press exits
    BackHandler {
        if (showEpisodeList) {
            viewModel.stopPlayback()
            navController.popBackStack()
        } else {
            showEpisodeList = true
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
                    when (keyEvent.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                            if (showEpisodeList) false
                            else { viewModel.playerManager.togglePlayPause(); true }
                        }
                        KeyEvent.KEYCODE_DPAD_UP -> {
                            if (!showEpisodeList) { viewModel.playerManager.next(); true } else false
                        }
                        KeyEvent.KEYCODE_DPAD_DOWN -> {
                            if (!showEpisodeList) { viewModel.playerManager.previous(); true } else false
                        }
                        else -> false
                    }
                } else false
            }
    ) {
        if (hasPlayableContent) {
            // Video Player
            VideoPlayer(
                viewModel = viewModel,
                useController = !showEpisodeList,
                resizeMode = resizeMode,
                onControllerVisibilityChanged = { isControllerVisible = it }
            )
        } else {
            // No playable content
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.ErrorOutline, null, tint = GoldPrimary, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No playable episodes found", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("This series may not have any available streams", color = Color.Gray, fontSize = 14.sp)
                }
            }
        }

        // Dim background when list is shown
        if (showEpisodeList) {
            LaunchedEffect(showEpisodeList, userInteractionTime) {
                kotlinx.coroutines.delay(15000)
                showEpisodeList = false
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showEpisodeList = false }
            )
        }

        // Episode List Overlay (Left Sidebar)
        AnimatedVisibility(
            visible = showEpisodeList,
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it })
        ) {
            Surface(
                modifier = Modifier.fillMaxHeight().width(380.dp),
                color = Color(0xFF0B0F1A).copy(alpha = 0.95f),
                tonalElevation = 8.dp
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PlayCircle, null, tint = GoldPrimary)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                seriesGroup.name, color = Color.White, fontSize = 18.sp,
                                fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "${allEpisodes.size} Episodes · ${allSeasons.size} Season${if (allSeasons.size > 1) "s" else ""}",
                                color = GoldPrimary, fontSize = 12.sp
                            )
                        }
                    }

                    // Season Tabs
                    if (allSeasons.size > 1) {
                        LazyRow(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(allSeasons) { season ->
                                val isSelected = season == selectedSeason
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) GoldPrimary else Color.White.copy(alpha = 0.08f))
                                        .clickable { selectedSeason = season; userInteractionTime = System.currentTimeMillis() }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        season,
                                        color = if (isSelected) Color.Black else Color.White,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    HorizontalDivider(color = GoldPrimary.copy(alpha = 0.2f), thickness = 1.dp, modifier = Modifier.padding(horizontal = 16.dp))

                    // Auto-scroll to playing episode
                    val playingUrl = viewModel.playerManager.currentUrl
                    LaunchedEffect(showEpisodeList, episodesForSeason) {
                        if (showEpisodeList) {
                            val activeIndex = episodesForSeason.indexOfFirst { it.streamUrl == playingUrl }
                            if (activeIndex >= 0) listState.scrollToItem(activeIndex)
                            kotlinx.coroutines.delay(100)
                            try { focusRequester.requestFocus() } catch (_: Exception) {}
                        }
                    }

                    val seasonEpisodes = episodesForSeason.filter { it.streamUrl.isNotBlank() }

                    if (seasonEpisodes.isEmpty()) {
                        Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Text("No episodes available", color = Color.Gray, fontSize = 14.sp)
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(seasonEpisodes) { episode ->
                                val isPlaying = episode.streamUrl == playingUrl
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isPlaying) GoldPrimary.copy(alpha = 0.15f) else Color.Transparent)
                                        .then(if (isPlaying) Modifier.focusRequester(focusRequester) else Modifier)
                                        .clickable {
                                            viewModel.playSeriesEpisode(episode, allEpisodes)
                                            userInteractionTime = System.currentTimeMillis()
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp))
                                            .background(if (isPlaying) GoldPrimary.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isPlaying) {
                                            Icon(Icons.Default.PlayArrow, null, tint = GoldPrimary, modifier = Modifier.size(24.dp))
                                        } else {
                                            Text(
                                                episode.episodeNum ?: (seasonEpisodes.indexOf(episode) + 1).toString(),
                                                color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            episode.name,
                                            color = if (isPlaying) GoldPrimary else Color.White,
                                            fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 14.sp, maxLines = 2, overflow = TextOverflow.Ellipsis
                                        )
                                        if (episode.season != null || episode.episodeNum != null) {
                                            Text(
                                                listOfNotNull(episode.season, episode.episodeNum).joinToString(" · "),
                                                color = if (isPlaying) GoldPrimary.copy(alpha = 0.7f) else Color.Gray,
                                                fontSize = 11.sp
                                            )
                                        }
                                        if (isPlaying) Text("Now Playing", color = GoldPrimary, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick Controls
        AnimatedVisibility(
            visible = isControllerVisible && !showEpisodeList,
            enter = fadeIn(), exit = fadeOut(),
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 32.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                ControlIconButton(
                    icon = if (resizeMode == MediaPlayer.ScaleType.SURFACE_BEST_FIT) Icons.Default.AspectRatio else Icons.Default.Fullscreen,
                    label = "Aspect",
                    onClick = {
                        resizeMode = if (resizeMode == MediaPlayer.ScaleType.SURFACE_BEST_FIT)
                            MediaPlayer.ScaleType.SURFACE_FILL else MediaPlayer.ScaleType.SURFACE_BEST_FIT
                    }
                )
            }
        }

        // Feedback Text
        if (!showEpisodeList && isControllerVisible) {
            Text(
                "Press BACK for Episode List",
                color = Color.White.copy(alpha = 0.5f), fontSize = 12.sp,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp)
            )
        }
    }
}
