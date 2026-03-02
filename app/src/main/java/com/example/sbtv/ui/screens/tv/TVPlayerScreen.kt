package com.example.sbtv.ui.screens.tv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun TVPlayerScreen(
    navController: NavController,
    streamUrl: String? = null,
    viewModel: TVViewModel = viewModel()
) {
    val channels by viewModel.channels.collectAsState()

    LaunchedEffect(streamUrl, channels) {
        if (streamUrl != null) {
            viewModel.playerManager.play(streamUrl)
        } else if (channels.isNotEmpty()) {
            viewModel.playLastOrDefault()
        }
    }

    BackHandler {
        viewModel.stopPlayback()
        if (streamUrl != null) {
            navController.popBackStack()
        } else {
            navController.navigate("channel_list")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        VideoPlayer(viewModel, useController = streamUrl != null)

        Text(
            text = if (streamUrl != null) "Press Back for Movies" else "Press Back for Channels",
            color = Color.White,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp)
        )
    }
}
