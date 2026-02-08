package com.chaddy50.musicapp.ui.modalSheets.nowPlayingSheet.composables

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun AlbumArtwork(currentTrack: MediaItem?) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(currentTrack?.mediaMetadata?.artworkUri?.let { "file://$it".toUri() })
            .crossfade(true)
            .build(),
        contentDescription = "Album artwork",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(320.dp)
    )
}