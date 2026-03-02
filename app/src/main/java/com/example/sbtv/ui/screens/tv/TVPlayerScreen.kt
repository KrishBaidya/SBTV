package com.example.sbtv.ui.screens.tv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun TVPlayerScreen(navController: NavController) {

    BackHandler {
        navController.navigate("channel_list")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        // TODO: Insert ExoPlayer View here

        Text(
            text = "Video Player Here",
            color = Color.White,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}