package com.example.sbtv.ui.screens.tv

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sbtv.data.local.ProviderViewModel
import com.example.sbtv.data.model.IPTVProvider
import com.example.sbtv.data.model.ProviderType
import java.util.UUID
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun AddPlaylistScreen(
    viewModel: ProviderViewModel = viewModel(),
    navController: NavController
) {

    var selectedType by remember { mutableStateOf(ProviderType.M3U) }

    var name by remember { mutableStateOf("") }
    var baseUrl by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text("Add Playlist", fontSize = 22.sp)

        // Toggle
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {

            FilterChip(
                selected = selectedType == ProviderType.M3U,
                onClick = { selectedType = ProviderType.M3U },
                label = { Text("M3U") }
            )

            FilterChip(
                selected = selectedType == ProviderType.XTREAM,
                onClick = { selectedType = ProviderType.XTREAM },
                label = { Text("Xtream") }
            )
        }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Provider Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = baseUrl,
            onValueChange = { baseUrl = it },
            label = { Text("Base URL or M3U URL") },
            modifier = Modifier.fillMaxWidth()
        )

        if (selectedType == ProviderType.XTREAM) {

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val provider = IPTVProvider(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    type = selectedType,
                    baseUrl = baseUrl,
                    username = if (selectedType == ProviderType.XTREAM) username else null,
                    password = if (selectedType == ProviderType.XTREAM) password else null
                )

                viewModel.addProvider(provider)
                navController.navigate("tv_player") {
                    popUpTo("add_playlist") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save")
        }
    }
}
