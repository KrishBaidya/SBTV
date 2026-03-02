package com.example.sbtv.ui.screens.tv

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController) {

    val items = listOf(
        "TV" to "tv_player",
        "Movies" to "movies",
        "Web Series" to "series",
        "Settings" to "settings"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(items) { item ->
                HomeButton(
                    title = item.first
                ) {
                    navController.navigate(item.second)
                }
            }
        }
    }
}

@Composable
fun HomeButton(title: String, onClick: () -> Unit) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 24.dp)
        ) {
            Text(
                text = title,
                fontSize = 22.sp,
                color = Color.White
            )
        }
    }
}