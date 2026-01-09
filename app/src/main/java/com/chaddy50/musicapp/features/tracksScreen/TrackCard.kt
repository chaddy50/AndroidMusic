package com.chaddy50.musicapp.features.tracksScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chaddy50.musicapp.data.entity.Track
import java.text.SimpleDateFormat

@Composable
fun TrackCard(
    track: Track,
    onTrackClicked: (Track) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clickable{ onTrackClicked(track) }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(10.dp, 0.dp)
                    .width(20.dp)
            ) {
                Text(track.number.toString())
            }
            Column (modifier = Modifier.weight(1f)){
                Text(track.title)
            }
            Column(
                modifier = Modifier
                    .widthIn(min = 40.dp)
                    .padding(10.dp, 0.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(SimpleDateFormat("m:ss").format(track.duration.inWholeMilliseconds))
            }
        }
    }
}