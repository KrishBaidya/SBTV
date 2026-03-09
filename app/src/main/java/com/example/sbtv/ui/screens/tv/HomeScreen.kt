package com.example.sbtv.ui.screens.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.graphics.vector.ImageVector
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

@Composable
fun HomeScreen(navController: NavController) {
    val items = listOf(
        HomeItem("TV", "tv_player", Icons.Default.Tv, MaterialTheme.colorScheme.primaryContainer),
        HomeItem("Movies", "movies", Icons.Default.Movie, MaterialTheme.colorScheme.secondaryContainer),
        HomeItem("Web Series", "series", Icons.Default.PlayArrow, MaterialTheme.colorScheme.tertiaryContainer),
        HomeItem("Settings", "settings", Icons.Default.Settings, MaterialTheme.colorScheme.surfaceVariant)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Background Gradient for Expressive feel
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp)
        ) {
            Text(
                text = "SBTV",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = "Your premium entertainment hub",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 40.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items) { item ->
                    HomeExpressiveCard(item) {
                        navController.navigate(item.route)
                    }
                }
            }
        }
    }
}

data class HomeItem(
    val title: String,
    val route: String,
    val icon: ImageVector,
    val containerColor: Color
)

@Composable
fun HomeExpressiveCard(item: HomeItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(28.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = item.containerColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                .padding(24.dp)
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                modifier = Modifier
                    .size(48.dp)
                    .align(Alignment.TopEnd),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = item.title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.BottomStart)
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
