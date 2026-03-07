package com.example.sbtv.ui.screens.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.sbtv.data.local.ProviderViewModel
import com.example.sbtv.data.model.IPTVProvider
import com.example.sbtv.data.model.ProviderType
import com.example.sbtv.ui.theme.GoldPrimary
import com.example.sbtv.ui.theme.LoginGradientEnd
import com.example.sbtv.ui.theme.LoginGradientStart
import com.example.sbtv.ui.theme.SurfaceDarkAlpha
import kotlinx.coroutines.launch
import java.util.UUID

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
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            errorMessage = null
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFFB00020),
                    contentColor = Color.White
                )
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            // Background Image
            AsyncImage(
                model = "https://images.unsplash.com/photo-1574375927938-d5a98e8ffe85?q=80&w=2669&auto=format&fit=crop",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Dark gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(LoginGradientStart, LoginGradientEnd)
                        )
                    )
            )

            // Form Card
            Card(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth(0.92f)
                    .verticalScroll(rememberScrollState()),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceDarkAlpha),
                elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "SB TV",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GoldPrimary,
                        style = androidx.compose.ui.text.TextStyle(
                            shadow = androidx.compose.ui.graphics.Shadow(
                                color = GoldPrimary.copy(alpha = 0.5f),
                                blurRadius = 24f
                            )
                        )
                    )
                    Text(
                        text = "Connect your playlist to start streaming",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        FilterChip(
                            selected = selectedType == ProviderType.M3U,
                            onClick = { selectedType = ProviderType.M3U },
                            label = { Text("M3U") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldPrimary.copy(alpha = 0.2f),
                                selectedLabelColor = GoldPrimary
                            )
                        )
                        FilterChip(
                            selected = selectedType == ProviderType.XTREAM,
                            onClick = { selectedType = ProviderType.XTREAM },
                            label = { Text("Xtream Codes") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoldPrimary.copy(alpha = 0.2f),
                                selectedLabelColor = GoldPrimary
                            )
                        )
                    }

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Playlist Name") },
                        placeholder = { Text("My TV") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        leadingIcon = { Icon(Icons.Default.Tv, null, tint = GoldPrimary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            focusedLabelColor = GoldPrimary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    OutlinedTextField(
                        value = baseUrl,
                        onValueChange = { baseUrl = it.trim() },
                        label = { Text(if (selectedType == ProviderType.M3U) "M3U URL" else "Server URL (http://host:port)") },
                        placeholder = { Text("http://example.com:8080") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp),
                        leadingIcon = { Icon(Icons.Default.Link, null, tint = GoldPrimary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            focusedLabelColor = GoldPrimary,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )

                    if (selectedType == ProviderType.XTREAM) {
                        OutlinedTextField(
                            value = username,
                            onValueChange = { username = it.trim() },
                            label = { Text("Username") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = GoldPrimary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                focusedLabelColor = GoldPrimary,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it.trim() },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            visualTransformation = PasswordVisualTransformation(),
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = GoldPrimary) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                focusedLabelColor = GoldPrimary,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (baseUrl.isBlank()) {
                                errorMessage = "Please enter a valid URL."
                                return@Button
                            }
                            isLoading = true
                            scope.launch {
                                val provider = IPTVProvider(
                                    id = UUID.randomUUID().toString(),
                                    name = name.ifBlank { "My Playlist" },
                                    type = selectedType,
                                    baseUrl = baseUrl,
                                    username = if (selectedType == ProviderType.XTREAM) username else null,
                                    password = if (selectedType == ProviderType.XTREAM) password else null
                                )
                                val isValid = viewModel.testConnection(provider)
                                if (isValid) {
                                    viewModel.addProvider(provider)
                                    navController.navigate("home") {
                                        popUpTo("add_playlist") { inclusive = true }
                                    }
                                } else {
                                    isLoading = false
                                    errorMessage = "❌ Could not connect. Check your URL or credentials and try again."
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = Color.Black,
                            disabledContainerColor = GoldPrimary.copy(alpha = 0.5f)
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Connecting...", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        } else {
                            Text("Connect & Stream", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}