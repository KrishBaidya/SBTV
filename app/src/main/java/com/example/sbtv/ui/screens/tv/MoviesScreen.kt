package com.example.sbtv.ui.screens.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MoviesScreen() {

    val movies = List(20) { "Movie $it" }

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        items(movies.size) { index ->
            MovieCard(title = movies[index])
        }
    }
}

@Composable
fun MovieCard(title: String) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        Box(
            modifier = Modifier
                .aspectRatio(0.7f)
                .background(Color.DarkGray, RoundedCornerShape(12.dp))
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            color = Color.White,
            fontSize = 14.sp,
            maxLines = 1
        )
    }
}