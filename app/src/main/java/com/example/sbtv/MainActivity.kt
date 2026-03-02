package com.example.sbtv

import android.app.UiModeManager
import android.content.Context
import android.content.Context.UI_MODE_SERVICE
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.compose.rememberNavController
import com.example.sbtv.ui.theme.SBTVTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SBTVTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (isTv()) {
                        TVApp(modifier = Modifier.padding(innerPadding))
                    } else {
                        PhoneApp(modifier = Modifier.padding(innerPadding))
                    }
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
fun PhoneApp(modifier: Modifier = Modifier) {
    Text("Helloo!!")
}

@Composable
fun TVApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    AppNavHost(
        navController = navController
    )
}

@Preview(showBackground = true)
@Composable
fun PhoneAppPreview() {
    SBTVTheme {
        PhoneApp()
    }
}

@Preview(showBackground = true)
@Composable
fun TvAppPreview() {
    SBTVTheme {
        TVApp()
    }
}