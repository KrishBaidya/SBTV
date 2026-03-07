package com.example.sbtv.ui.screens.tv

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import com.example.sbtv.data.model.Channel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sbtv.ui.theme.GoldPrimary
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ChannelListScreen(
    navController: NavController,
    viewModel: TVViewModel = viewModel()
) {
    val channels by viewModel.channels.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Build group list for tabs
    val customCategories by viewModel.customCategories.collectAsState(initial = emptyList())
    val groups = remember(channels, customCategories) {
        val baseGroups = channels.mapNotNull { it.group }.distinct()
        val customGroups = customCategories.map { it.name }
        // Place "All" first, then custom groups, then provider base groups
        (listOf("All") + customGroups + baseGroups).distinct()
    }
    
    var showCreateGroupDialog by remember { mutableStateOf(false) }
    var newGroupName by remember { mutableStateOf("") }
    
    var showGroupOptionsDialog by remember { mutableStateOf<String?>(null) }
    // Pair of (oldGroupName, categoryId) for the rename dialog
    var showRenameGroupDialog by remember { mutableStateOf<Pair<String, String>?>(null) }
    var renameGroupName by remember { mutableStateOf("") }
    
    var showChannelOptionsDialog by remember { mutableStateOf<Channel?>(null) }
    
    var showBulkManageDialog by remember { mutableStateOf<com.example.sbtv.data.model.CustomCategory?>(null) }
    var bulkSearchQuery by remember { mutableStateOf("") }
    
    var selectedGroup by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }

    val filtered = remember(channels, selectedGroup, searchQuery) {
        channels
            .filter { selectedGroup == "All" || it.group == selectedGroup }
            .filter { searchQuery.isBlank() || it.name.contains(searchQuery, ignoreCase = true) }
    }

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
                Text("Live TV", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${channels.size} Channels Available", color = GoldPrimary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(16.dp))
                    TextButton(onClick = { navController.navigate("manage_playlists") }) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Manage Playlists", color = GoldPrimary)
                    }
                }
            }
            
            // Search Bar (more compact for TV)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .width(300.dp)
                    .height(56.dp),
                placeholder = { Text("Search...", color = Color.Gray, fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp)) },
                singleLine = true,
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.3f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = GoldPrimary,
                    focusedContainerColor = Color(0xFF1A1F2B),
                    unfocusedContainerColor = Color(0xFF1A1F2B)
                )
            )
        }

        // Category Tabs
        if (groups.size > 1) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(bottom = 24.dp).weight(1f)
                ) {
                    items(groups) { group ->
                        val isSelected = group == selectedGroup
                        val isCustom = customCategories.any { it.name == group }
                        val bgColor by animateColorAsState(
                            if (isSelected) GoldPrimary else Color.White.copy(alpha = 0.05f),
                            label = "chip"
                        )
                        @OptIn(ExperimentalFoundationApi::class)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(bgColor)
                                .combinedClickable(
                                    onClick = { selectedGroup = group },
                                    onLongClick = { if (isCustom) showGroupOptionsDialog = group }
                                )
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = group,
                                color = if (isSelected) Color.Black else Color.White,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Add Custom Group Button
                Button(
                    onClick = { showCreateGroupDialog = true },
                    modifier = Modifier.padding(bottom = 24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("+ List", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Content
        if (isLoading && channels.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GoldPrimary)
            }
        } else if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No channels found", color = Color.Gray, fontSize = 16.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(filtered, key = { it.id }) { channel ->
                    @OptIn(ExperimentalFoundationApi::class)
                    Box(
                        modifier = Modifier.combinedClickable(
                            onClick = {
                                val encoded = URLEncoder.encode(channel.streamUrl, StandardCharsets.UTF_8.toString())
                                navController.navigate("tv_player?url=$encoded&fromTV=true")
                            },
                            onLongClick = {
                                showChannelOptionsDialog = channel
                            }
                        )
                    ) {
                        ChannelItem(channel = channel, onClick = {
                            val encoded = URLEncoder.encode(channel.streamUrl, StandardCharsets.UTF_8.toString())
                            navController.navigate("tv_player?url=$encoded&fromTV=true")
                        })
                    }
                }
            }
        }
    }
    
    // --- DIALOGS ---
    
    if (showCreateGroupDialog) {
        AlertDialog(
            onDismissRequest = { showCreateGroupDialog = false },
            title = { Text("Create Custom List", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = newGroupName,
                    onValueChange = { newGroupName = it },
                    placeholder = { Text("e.g. My Favorites") },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newGroupName.isNotBlank()) {
                        viewModel.addCustomCategory(newGroupName)
                        newGroupName = ""
                        showCreateGroupDialog = false
                    }
                }) { Text("Create", color = GoldPrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showCreateGroupDialog = false }) { Text("Cancel", color = Color.Gray) }
            },
            containerColor = Color(0xFF1A1F2B)
        )
    }

    if (showGroupOptionsDialog != null) {
        val groupName = showGroupOptionsDialog!!
        val cat = customCategories.find { it.name == groupName }
        if (cat != null) {
            AlertDialog(
                onDismissRequest = { showGroupOptionsDialog = null },
                title = { Text("Manage '$groupName'", color = Color.White) },
                text = {
                    Column {
                        TextButton(
                            onClick = { 
                                renameGroupName = groupName
                                showRenameGroupDialog = groupName to cat.id
                                showGroupOptionsDialog = null 
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Rename List", color = Color.White) }
                        
                        TextButton(
                            onClick = { 
                                showBulkManageDialog = cat
                                showGroupOptionsDialog = null 
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Manage Channels", color = GoldPrimary) }

                        TextButton(
                            onClick = {
                                viewModel.deleteCustomCategory(cat.id)
                                if (selectedGroup == groupName) selectedGroup = "All"
                                showGroupOptionsDialog = null
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Delete List", color = Color.Red) }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showGroupOptionsDialog = null }) { Text("Close", color = Color.Gray) }
                },
                containerColor = Color(0xFF1A1F2B)
            )
        } else {
            showGroupOptionsDialog = null
        }
    }
    
    if (showRenameGroupDialog != null) {
        AlertDialog(
            onDismissRequest = { showRenameGroupDialog = null },
            title = { Text("Rename List", color = Color.White) },
            text = {
                OutlinedTextField(
                    value = renameGroupName,
                    onValueChange = { renameGroupName = it },
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (renameGroupName.isNotBlank()) {
                        val (oldName, catId) = showRenameGroupDialog!!
                        viewModel.renameCustomCategory(catId, renameGroupName)
                        if (selectedGroup == oldName) selectedGroup = renameGroupName
                        showRenameGroupDialog = null
                    }
                }) { Text("Save", color = GoldPrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showRenameGroupDialog = null }) { Text("Cancel", color = Color.Gray) }
            },
            containerColor = Color(0xFF1A1F2B)
        )
    }

    if (showChannelOptionsDialog != null) {
        val channel = showChannelOptionsDialog!!
        AlertDialog(
            onDismissRequest = { showChannelOptionsDialog = null },
            title = { Text("Manage '${channel.name}'", color = Color.White) },
            text = {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { Text("Tap below to add or remove this channel from your custom lists.", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp)) }
                    items(customCategories) { cat ->
                        val isInCat = cat.channelIds.contains(channel.id)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isInCat) GoldPrimary.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f))
                                .clickable {
                                    viewModel.toggleChannelInCustomCategory(cat.id, channel.id)
                                    // Keep open to allow toggling multiple categories
                                }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(cat.name, color = if (isInCat) GoldPrimary else Color.White)
                            if (isInCat) Text("Remove", color = Color.Red, fontSize = 12.sp)
                            else Text("Add", color = GoldPrimary, fontSize = 12.sp)
                        }
                    }
                    if (customCategories.isEmpty()) {
                        item { Text("No custom lists created yet.", color = Color.Gray) }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showChannelOptionsDialog = null }) { Text("Done", color = GoldPrimary) }
            },
            containerColor = Color(0xFF1A1F2B)
        )
    }

    if (showBulkManageDialog != null) {
        val category = showBulkManageDialog!!
        // Use live customCategories flow so toggling updates the UI reactively
        val liveCats by viewModel.customCategories.collectAsState(initial = emptyList())
        val categoryChannels = liveCats.find { it.id == category.id }?.channelIds ?: emptyList()
        
        AlertDialog(
            onDismissRequest = { showBulkManageDialog = null },
            title = { 
                Column {
                    Text("Manage Channels in '${category.name}'", color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = bulkSearchQuery,
                        onValueChange = { bulkSearchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search channels...", color = Color.Gray) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = GoldPrimary) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }
            },
            text = {
                val searchResult = remember(channels, bulkSearchQuery) {
                    channels.filter { it.name.contains(bulkSearchQuery, ignoreCase = true) }
                }
                
                Box(modifier = Modifier.height(400.dp)) {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(searchResult) { channel ->
                            val isInList = categoryChannels.contains(channel.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isInList) GoldPrimary.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.05f))
                                    .clickable { viewModel.toggleChannelInCustomCategory(category.id, channel.id) }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(channel.name, color = if (isInList) GoldPrimary else Color.White, modifier = Modifier.weight(1f))
                                if (isInList) Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { 
                    showBulkManageDialog = null 
                    bulkSearchQuery = ""
                }) { Text("Done", color = GoldPrimary) }
            },
            containerColor = Color(0xFF1A1F2B)
        )
    }
}
