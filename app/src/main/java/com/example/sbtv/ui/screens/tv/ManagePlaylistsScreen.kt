package com.example.sbtv.ui.screens.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sbtv.data.local.ProviderViewModel
import com.example.sbtv.data.model.IPTVProvider
import com.example.sbtv.ui.theme.GoldPrimary
import com.example.sbtv.ui.theme.SurfaceDarkAlpha

@Composable
fun ManagePlaylistsScreen(
    navController: NavController,
    viewModel: ProviderViewModel = viewModel()
) {
    val providers by viewModel.providers.collectAsState(initial = emptyList())
    val activeProviderId by viewModel.activeProviderId.collectAsState(initial = null)

    var showRenameDialog by remember { mutableStateOf<IPTVProvider?>(null) }
    var newName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(horizontal = 48.dp)
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
                Text("Manage Playlists", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Select, rename, or delete your TV playlists", color = GoldPrimary, fontSize = 14.sp)
            }

            Button(
                onClick = { navController.navigate("add_playlist") },
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create new playlist", fontWeight = FontWeight.Bold)
            }
        }

        if (providers.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No playlists found. Create one to start watching.", color = Color.Gray, fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(providers) { provider ->
                    val isActive = provider.id == activeProviderId || (activeProviderId == null && provider == providers.firstOrNull())
                    
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isActive) GoldPrimary.copy(alpha = 0.15f) else SurfaceDarkAlpha)
                            .clickable {
                                viewModel.setActiveProvider(provider.id)
                            }
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Tv, contentDescription = null, tint = if (isActive) GoldPrimary else Color.White, modifier = Modifier.size(32.dp))
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = provider.name,
                                color = if (isActive) GoldPrimary else Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${provider.type} • ${provider.baseUrl}",
                                color = Color.Gray,
                                fontSize = 14.sp
                            )
                        }

                        // Actions
                        IconButton(onClick = {
                            newName = provider.name
                            showRenameDialog = provider
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Rename", tint = Color.Gray)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(onClick = { viewModel.deleteProvider(provider.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFE53935))
                        }
                    }
                }
            }
        }
    }

    if (showRenameDialog != null) {
        AlertDialog(
            onDismissRequest = { showRenameDialog = null },
            title = { Text("Rename Playlist", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showRenameDialog?.id?.let {
                        viewModel.renameProvider(it, newName)
                    }
                    showRenameDialog = null
                }) {
                    Text("Save", color = GoldPrimary)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = SurfaceDarkAlpha
        )
    }
}
