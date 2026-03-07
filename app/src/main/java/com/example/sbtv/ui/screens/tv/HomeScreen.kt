package com.example.sbtv.ui.screens.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.sbtv.data.model.Channel
import com.example.sbtv.data.model.Movie
import com.example.sbtv.data.model.Series
import com.example.sbtv.ui.theme.GoldPrimary
import com.example.sbtv.ui.theme.SurfaceDark

// Mock Data from Figma
data class MediaItem(val id: String, val title: String, val image: String, val isPoster: Boolean = true)

val TRENDING = listOf(
    MediaItem("t1", "Inception", "https://images.unsplash.com/photo-1596727147705-0608c643e34b?auto=format&fit=crop&q=80&w=300&h=450"),
    MediaItem("t2", "The Matrix", "https://images.unsplash.com/photo-1626814026160-2237a95fc5a0?auto=format&fit=crop&q=80&w=300&h=450"),
    MediaItem("t3", "Interstellar", "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?auto=format&fit=crop&q=80&w=300&h=450"),
    MediaItem("t4", "Blade Runner 2049", "https://images.unsplash.com/photo-1485846234645-a62644f84728?auto=format&fit=crop&q=80&w=300&h=450"),
    MediaItem("t5", "Dune", "https://images.unsplash.com/photo-1541963463532-d68292c34b19?auto=format&fit=crop&q=80&w=300&h=450")
)

val TV_CHANNELS = listOf(
    MediaItem("tv1", "News 24", "https://images.unsplash.com/photo-1585829365295-ab7cd400c167?auto=format&fit=crop&q=80&w=400&h=250", false),
    MediaItem("tv2", "Sports Live", "https://images.unsplash.com/photo-1540747913346-19e32dc3e97e?auto=format&fit=crop&q=80&w=400&h=250", false),
    MediaItem("tv3", "Kids Cartoon", "https://images.unsplash.com/photo-1560169897-fc0cdbdfa4d5?auto=format&fit=crop&q=80&w=400&h=250", false)
)

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: TVViewModel = viewModel()
) {
    val channels by viewModel.channels.collectAsState()
    val movies by viewModel.movies.collectAsState()
    val series by viewModel.series.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    if (isLoading && channels.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = GoldPrimary)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            // Hero Section (Featured Content)
            item {
                val featured = movies.firstOrNull() ?: channels.firstOrNull()
                HeroSection(featured, navController)
            }

            // Categories
            if (channels.isNotEmpty()) {
                item {
                    ContentRow(
                        title = "Live Channels",
                        items = channels.take(10).map { MediaItem(it.id, it.name, it.logo ?: "", isPoster = false) },
                        navController = navController,
                        route = "channel_list"
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            if (movies.isNotEmpty()) {
                item {
                    ContentRow(
                        title = "Trending Movies",
                        items = movies.take(10).map { MediaItem(it.id, it.name, it.poster ?: "") },
                        navController = navController,
                        route = "movies"
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            if (series.isNotEmpty()) {
                item {
                    ContentRow(
                        title = "Popular Series",
                        items = series.take(10).map { MediaItem(it.id, it.name, it.poster ?: "") },
                        navController = navController,
                        route = "series"
                    )
                    Spacer(modifier = Modifier.height(48.dp))
                }
            }
            
            // If empty, show placeholder
            if (channels.isEmpty() && movies.isEmpty() && series.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No content found. Please check your playlist.", color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun HeroSection(item: Any?, navController: NavController) {
    val title = when (item) {
        is Movie -> item.name
        is Channel -> item.name
        is Series -> item.name
        else -> "Welcome to SB TV"
    }
    
    val imageUrl = when (item) {
        is Movie -> item.poster
        is Channel -> item.logo
        is Series -> item.poster
        else -> "https://images.unsplash.com/photo-1534447677768-be436bb09401?q=80&w=2694&auto=format&fit=crop"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(450.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient Box Overlay (Netflix style)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xFF0B0F1A)),
                        startY = 200f
                    )
                )
        )
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color(0xFF0B0F1A).copy(alpha = 0.8f), Color.Transparent),
                        startX = 0f,
                        endX = 1000f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = "Featured Content",
                color = GoldPrimary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 48.sp,
                modifier = Modifier.fillMaxWidth(0.6f)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { 
                        when (item) {
                            is Channel -> {
                                val encoded = java.net.URLEncoder.encode(item.streamUrl, java.nio.charset.StandardCharsets.UTF_8.toString())
                                navController.navigate("tv_player?url=$encoded&fromTV=true")
                            }
                            is Movie -> {
                                val encoded = java.net.URLEncoder.encode(item.streamUrl, java.nio.charset.StandardCharsets.UTF_8.toString())
                                navController.navigate("tv_player?url=$encoded&fromTV=false")
                            }
                            else -> navController.navigate("channel_list")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Watch Now", fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = { },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("More Info")
                }
            }
        }
    }
}

@Composable
fun ContentRow(title: String, items: List<MediaItem>, navController: NavController, route: String) {
    Column(modifier = Modifier.padding(top = 24.dp)) {
        Row(
            modifier = Modifier.padding(horizontal = 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(18.dp)
                    .background(GoldPrimary, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "See All",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.clickable { navController.navigate(route) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 48.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                Column(
                    modifier = Modifier.clickable { navController.navigate(route) }
                ) {
                    Card(
                        modifier = Modifier
                            .width(if (item.isPoster) 160.dp else 240.dp)
                            .height(if (item.isPoster) 240.dp else 135.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1F2B))
                    ) {
                        if (item.image.isNotEmpty()) {
                            AsyncImage(
                                model = item.image,
                                contentDescription = item.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Tv, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(48.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = item.title,
                        color = Color.LightGray,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        modifier = Modifier.width(if (item.isPoster) 160.dp else 240.dp)
                    )
                }
            }
        }
    }
}