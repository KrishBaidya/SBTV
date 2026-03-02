package com.example.sbtv

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sbtv.ui.screens.tv.ChannelListScreen
import com.example.sbtv.ui.screens.tv.HomeScreen
import com.example.sbtv.ui.screens.tv.MoviesScreen
import com.example.sbtv.ui.screens.tv.SeriesScreen
import com.example.sbtv.ui.screens.tv.TVPlayerScreen

@Composable
fun AppNavHost(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        composable("home") {
            HomeScreen(navController)
        }

        composable("tv_player") {
            TVPlayerScreen(navController)
        }

        composable("channel_list") {
            ChannelListScreen(navController)
        }

        composable("movies") {
            MoviesScreen()
        }

        composable("series") {
            SeriesScreen()
        }
    }
}
