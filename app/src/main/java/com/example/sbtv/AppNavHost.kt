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
import java.net.URLEncoder
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
            HomeScreen(navController)
        }

        composable("add_playlist") {
            AddPlaylistScreen(navController = navController)
        }

        composable("channel_list") {
            ChannelListScreen(navController = navController, viewModel = tvViewModel)
        }

        composable(
            route = "tv_player?url={url}",
            arguments = listOf(navArgument("url") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("url")
            val url = encodedUrl?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
            TVPlayerScreen(navController = navController, streamUrl = url, viewModel = tvViewModel)
        }

        composable("movies") {
            MoviesScreen(
                navController = navController,
                onMovieClick = { movie ->
                    val encodedUrl = URLEncoder.encode(movie.streamUrl, StandardCharsets.UTF_8.toString())
                    navController.navigate("tv_player?url=$encodedUrl")
                }
            )
        }

        composable("series") {
            SeriesScreen(
                navController = navController,
                onSeriesClick = { series ->
                    val encodedUrl = URLEncoder.encode(series.streamUrl, StandardCharsets.UTF_8.toString())
                    navController.navigate("tv_player?url=$encodedUrl")
                }
            )
        }

        composable("settings") {
            SettingsScreen(navController = navController)
        }
        
        composable("manage_playlists") {
            ManagePlaylistsScreen(navController = navController)
        }
    }
}
