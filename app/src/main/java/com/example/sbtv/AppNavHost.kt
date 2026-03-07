package com.example.sbtv

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.sbtv.ui.screens.tv.AddPlaylistScreen
import com.example.sbtv.ui.screens.tv.ChannelListScreen
import com.example.sbtv.ui.screens.tv.HomeScreen
import com.example.sbtv.ui.screens.tv.ManagePlaylistsScreen
import com.example.sbtv.ui.screens.tv.MoviesScreen
import com.example.sbtv.ui.screens.tv.SeriesScreen
import com.example.sbtv.ui.screens.tv.SettingsScreen
import com.example.sbtv.ui.screens.tv.TVPlayerScreen
import com.example.sbtv.ui.screens.tv.TVViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = "home"
) {

    val tvViewModel: TVViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable("home") {
            HomeScreen(navController, viewModel = tvViewModel)
        }

        composable("add_playlist") {
            AddPlaylistScreen(navController = navController)
        }

        composable("channel_list") {
            ChannelListScreen(navController = navController, viewModel = tvViewModel)
        }

        composable("manage_playlists") {
            ManagePlaylistsScreen(navController = navController)
        }

        composable(
            route = "tv_player?url={url}&fromTV={fromTV}",
            arguments = listOf(
                navArgument("url") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("fromTV") {
                    type = NavType.BoolType
                    defaultValue = true
                }
            )
        ) { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("url")
            val url = encodedUrl?.ifBlank { null }?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
            val fromTV = backStackEntry.arguments?.getBoolean("fromTV") ?: true
            TVPlayerScreen(navController = navController, streamUrl = url, fromTV = fromTV, viewModel = tvViewModel)
        }

        composable("movies") {
            MoviesScreen(navController = navController, viewModel = tvViewModel)
        }

        composable("series") {
            SeriesScreen(navController = navController, viewModel = tvViewModel)
        }

        composable("settings") {
            SettingsScreen()
        }
    }
}
