package com.chaddy50.musicapp.ui.modalSheets.nowPlayingSheet.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaddy50.musicapp.utilities.formatMillisecondsIntoMinutesAndSeconds

@Composable
fun ProgressBar(
    playbackPosition: Long,
    durationMs: Long,
    onSeek: (Long) -> Unit,
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragPosition by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
    ) {
        val progress = if (durationMs > 0) playbackPosition.toFloat() / durationMs.toFloat() else 0f

        Slider(
            value = if (isDragging) dragPosition else progress,
            onValueChange = { value ->
                isDragging = true
                dragPosition = value
            },
            onValueChangeFinished = {
                onSeek((dragPosition * durationMs).toLong())
                isDragging = false
            },
            modifier = Modifier.fillMaxWidth()
        )

        val displayPosition = if (isDragging) (dragPosition * durationMs).toLong() else playbackPosition

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = formatMillisecondsIntoMinutesAndSeconds(displayPosition),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formatMillisecondsIntoMinutesAndSeconds(durationMs),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
