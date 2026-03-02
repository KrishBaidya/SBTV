package com.example.sbtv.ui.screens.tv

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView

@Composable
fun VideoPlayer(viewModel: TVViewModel, useController: Boolean = false) {
    val context = LocalContext.current

    AndroidView(
        factory = {
            PlayerView(context).apply {
                player = viewModel.playerManager.player
                this.useController = useController
            }
        },
        update = { playerView ->
            playerView.player = viewModel.playerManager.player
            playerView.useController = useController
        },
        modifier = Modifier.fillMaxSize()
    )
}
