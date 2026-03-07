package com.example.sbtv.ui.screens.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sbtv.player.PlayerPreferenceManager
import com.example.sbtv.ui.theme.GoldPrimary

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val prefManager = remember { PlayerPreferenceManager(context) }
    var selectedPlayer by remember { mutableStateOf(prefManager.getSelectedPlayer()) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = Color(0xFF0B0F1A),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 48.dp, vertical = 32.dp)
        ) {
            // Header
            Text(
                text = "Settings",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Customize your SBTV experience",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 36.dp)
            )

            // ── Player Engine Section ─────────────────────────────────────────
            Text(
                text = "PLAYER ENGINE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = GoldPrimary,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            val options = listOf(
                PlayerPreferenceManager.PLAYER_VLC      to "VLC Player  (Default / Best compatibility)"
            )

            options.forEach { (value, label) ->
                PlayerEngineOption(
                    label = label,
                    isSelected = selectedPlayer == value,
                    onClick = {
                        if (selectedPlayer != value) {
                            selectedPlayer = value
                            prefManager.setSelectedPlayer(value)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "⚠  Restart playback for changes to take effect.",
                color = Color.Gray,
                fontSize = 13.sp
            )
        }
    }

    // Show snackbar only after the user changes selection (not on initial compose)
    var initialLoadDone by remember { mutableStateOf(false) }
    LaunchedEffect(selectedPlayer) {
        if (initialLoadDone) {
            snackbarHostState.showSnackbar(
                message = "Restart playback for changes to take effect",
                duration = SnackbarDuration.Short
            )
        }
        initialLoadDone = true
    }
}

@Composable
private fun PlayerEngineOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) GoldPrimary.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.05f)
    val borderColor = if (isSelected) GoldPrimary else Color.White.copy(alpha = 0.1f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = if (isSelected) GoldPrimary else Color.White,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 15.sp
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = GoldPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
