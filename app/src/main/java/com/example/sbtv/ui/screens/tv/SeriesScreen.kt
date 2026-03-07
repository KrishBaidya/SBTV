package com.example.sbtv.ui.screens.tv

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.sbtv.data.model.Series
import com.example.sbtv.ui.theme.GoldPrimary
import kotlinx.coroutines.launch

@Composable
fun SeriesScreen(
    navController: NavController,
    viewModel: TVViewModel = viewModel()
) {
    val seriesList by viewModel.series.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Build category list
    val categories = remember(seriesList) {
        listOf("All") + seriesList.mapNotNull { it.category }.distinct()
    }
    var selectedCategory by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(seriesList, selectedCategory, searchQuery) {
        seriesList
            .filter { selectedCategory == "All" || it.category == selectedCategory }
            .filter { searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 48.dp)
            .padding(padding)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Series", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("${seriesList.size} Shows Available", color = GoldPrimary, fontSize = 14.sp)
            }
            
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .width(300.dp)
                    .height(56.dp),
                placeholder = { Text("Search...", color = Color.Gray, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp)) },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = GoldPrimary,
                    focusedContainerColor = Color(0xFF1A1F2B),
                    unfocusedContainerColor = Color(0xFF1A1F2B)
                )
            )
        }

        // Category Tabs
        if (categories.size > 1) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = cat == selectedCategory
                    val bgColor by animateColorAsState(
                        if (isSelected) GoldPrimary else Color.White.copy(alpha = 0.05f),
                        label = "chip"
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgColor)
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = cat,
                            color = if (isSelected) Color.Black else Color.White,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        if (isLoading && seriesList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GoldPrimary)
            }
        } else if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No series found", color = Color.Gray, fontSize = 16.sp)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(filtered, key = { it.id }) { series ->
                    SeriesCard(series = series, onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                message = "Series episodes coming soon!",
                                duration = SnackbarDuration.Short
                            )
                        }
                    })
                }
            }
        }
    }
    } // end Scaffold
}

@Composable
fun SeriesCard(series: Series, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(2f / 3f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF1A1F2B))
        ) {
            if (!series.poster.isNullOrBlank()) {
                AsyncImage(
                    model = series.poster,
                    contentDescription = series.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.VideoLibrary, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(48.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = series.name,
            color = Color.LightGray,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}