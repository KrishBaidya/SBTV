package com.example.sbtv

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.sbtv.ui.screens.AppLayout
import com.example.sbtv.ui.theme.SBTVTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SBTVTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val isTv = isTv()
                    MainAppRenderer(modifier = Modifier.padding(innerPadding), isTv = isTv)
                }
            }
        }
    }
}

fun Context.isTv(): Boolean {
    val uiModeManager = getSystemService(UiModeManager::class.java)
    return uiModeManager.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
}

@Composable
fun MainAppRenderer(
    modifier: Modifier = Modifier,
    isTv: Boolean,
    viewModel: MainViewModel = viewModel()
) {
    val navController = rememberNavController()
    val hasProviders by viewModel.hasProviders.collectAsState(initial = null)

    if (hasProviders != null) {
        AppLayout(navController = navController, isTv = isTv) {
            AppNavHost(
                navController = navController,
                startDestination = if (hasProviders == true) "home" else "add_playlist"
            )
        }
    }
}
