package com.chaddy50.musicapp.ui.modalSheets.nowPlayingSheet.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem

@Composable
fun TrackInfo(currentTrack: MediaItem?) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = currentTrack?.mediaMetadata?.title?.toString() ?: "Unknown Title",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        val artistName = currentTrack?.mediaMetadata?.artist?.toString() ?: "Unknown Artist"
        val albumArtistName = currentTrack?.mediaMetadata?.albumArtist?.toString() ?: "Unknown Album Artist"
        val albumName = currentTrack?.mediaMetadata?.albumTitle?.toString() ?: "Unknown Album"

        Text(
            text = artistName,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth()
        )

        if (albumArtistName != artistName) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = albumArtistName,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (albumName != albumArtistName) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = albumName,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}