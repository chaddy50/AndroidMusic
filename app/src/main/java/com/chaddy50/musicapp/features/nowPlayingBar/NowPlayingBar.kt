package com.chaddy50.musicapp.features.nowPlayingBar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import coil.compose.AsyncImage

@Composable
fun NowPlayingBar(
    currentTrack: MediaItem?,
    isPlaying: Boolean,
    playbackPosition: Long,
    duration: Long,
    onPlayPauseClicked: () -> Unit,
    onSkipTrackClicked: () -> Unit,
) {
    val metadata = currentTrack?.mediaMetadata

    BottomAppBar {
        Column(
            modifier = Modifier
                .padding(top = 4.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            ) {
                AsyncImage(
                    model = metadata?.artworkData,
                    contentDescription = "Album art",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = rememberVectorPainter(Icons.Default.MusicNote)
                )

                Column(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = metadata?.title?.toString() ?: "Nothing Playing",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = metadata?.artist?.toString() ?: "Unknown Artist",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onPlayPauseClicked) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play"
                    )
                }

                IconButton(onClick = onSkipTrackClicked) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Skip to next track"
                    )
                }
            }

            val progress = if (duration > 0) playbackPosition.toFloat() / duration.toFloat() else 0f
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth(),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round,
            )
        }
    }
}

@Preview
@Composable
private fun NowPlayingBarPreview() {
    val metadata = MediaMetadata.Builder()
        .setTitle("No. 2 - Andante")
        .setArtist("Brahms - Jonathan Plowright")
        .setAlbumTitle("Op. 118 - Six Intermezzos")
        .build()

    val currentTrack = MediaItem.Builder()
        .setMediaMetadata(metadata)
        .build()

    NowPlayingBar(
        currentTrack,
        true,
        5,
        10,
        {},
        {}
    )
}