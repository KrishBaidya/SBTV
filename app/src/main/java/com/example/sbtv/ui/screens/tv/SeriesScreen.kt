package com.example.sbtv.ui.screens.tv

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.sbtv.data.model.Movie

@Composable
fun SeriesScreen(navController: NavController? = null, onSeriesClick: (Movie) -> Unit = {}) {
    // Currently relying on MoviesViewModel since they both might use the same logic if it's VOD.
    // Ideally, a separate ViewModel for Series would be used here.
    MoviesScreen(
        navController = navController,
        onMovieClick = onSeriesClick,
        title = "Web Series"
    )
}
