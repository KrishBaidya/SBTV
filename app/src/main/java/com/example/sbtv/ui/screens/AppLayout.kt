package com.example.sbtv.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.sbtv.isTv
import com.example.sbtv.ui.theme.DrawerGradientEnd
import com.example.sbtv.ui.theme.DrawerGradientStart
import com.example.sbtv.ui.theme.GoldPrimary

data class MenuItem(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

val MainMenuItems = listOf(
    MenuItem("home", "Home", Icons.Default.Home),
    MenuItem("channel_list", "TV", Icons.Default.Tv),
    MenuItem("movies", "Movies", Icons.Default.Movie),
    MenuItem("series", "Series", Icons.Default.VideoLibrary),
    MenuItem("settings", "Settings", Icons.Default.Settings)
)

@Composable
fun AppLayout(
    navController: NavController,
    isTv: Boolean,
    content: @Composable () -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""
    
    // Do not show navigation bars on full screen routes like player or login
    val isFullScreen = currentRoute.startsWith("tv_player") || currentRoute == "add_playlist"

    if (isFullScreen) {
        content()
        return
    }

    if (isTv) {
        Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0B0F1A))) {
            // Top Navigation Bar for TV
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(horizontal = 48.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo
                Text(
                    text = "SB TV",
                    color = GoldPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(end = 48.dp)
                )

                // Navigation Items
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MainMenuItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(if (isSelected) GoldPrimary.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable { navController.navigate(item.route) }
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.title,
                                    tint = if (isSelected) GoldPrimary else Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = item.title,
                                    color = if (isSelected) GoldPrimary else Color.Gray,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }

                // Profile / Settings Icon on the right
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF4A101C)) // Darker reddish color from Figma
                        .clickable { /* Profile Logic */ },
                    contentAlignment = Alignment.Center
                ) {
                    Text("SB", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            
            // Content
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                content()
            }
        }
    } else {
        // Phone Layout with Bottom Navigation (Polished)
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF0B0F1A),
                    contentColor = GoldPrimary,
                    tonalElevation = 8.dp
                ) {
                    MainMenuItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { navController.navigate(item.route) },
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = GoldPrimary,
                                unselectedIconColor = Color.Gray,
                                selectedTextColor = GoldPrimary,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = GoldPrimary.copy(alpha = 0.1f)
                            )
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize().background(Color(0xFF0B0F1A))) {
                content()
            }
        }
    }
}
