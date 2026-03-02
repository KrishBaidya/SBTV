package com.example.sbtv.ui.screens.tv

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ChannelListScreen(navController: NavController) {

    BackHandler {
        navController.navigate("home")
    }

    val channels = listOf(
        "HBO",
        "Star Sports",
        "Discovery",
        "National Geographic",
        "Cartoon Network"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(channels) { channel ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clickable {
                        // Save as last played
                        navController.navigate("tv_player")
                    },
                colors = CardDefaults.cardColors(
                    containerColor = Color.DarkGray
                )
            ) {
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 16.dp)
                ) {
                    Text(channel, color = Color.White)
                }
            }
        }
    }
}