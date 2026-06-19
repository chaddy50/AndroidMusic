package com.chaddy50.froh.ui.modalSheets.nowPlayingSheet.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlaybackControls(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onSkipToPreviousTrack: () -> Unit,
    onSkipToNextTrack: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledTonalIconButton(
            onClick = onSkipToPreviousTrack,
            modifier = Modifier.size(72.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Skip Previous",
                modifier = Modifier.size(36.dp)
            )
        }

        FilledIconButton(
            onClick = onPlayPause,
            modifier = Modifier.size(80.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                modifier = Modifier.size(40.dp)
            )
        }

        FilledTonalIconButton(
            onClick = onSkipToNextTrack,
            modifier = Modifier.size(72.dp)
        ) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Skip Next",
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
