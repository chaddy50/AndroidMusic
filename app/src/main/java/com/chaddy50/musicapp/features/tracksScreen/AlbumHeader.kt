package com.chaddy50.musicapp.features.tracksScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

@Composable
fun AlbumHeader(albumId: Int, viewModel: MusicAppViewModel) {
    if (albumId == 0) return

    val album by viewModel.getAlbumById(albumId).collectAsState()
    if (album == null) return

    Column(
        modifier = Modifier
            .padding(0.dp, 10.dp, 0.dp, 0.dp)
            .fillMaxSize()
    ) {
//        if (album.artwork != null) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(20.dp),
//                horizontalArrangement = Arrangement.Center
//            ) {
//                Image(
//                    bitmap = Bitmap.createScaledBitmap(album.artwork!!, 1000, 1000, false)
//                        .asImageBitmap(), contentDescription = "Album artwork"
//                )
//            }
//        }
        Row(modifier = Modifier.padding(10.dp, 3.dp)) {
            Text(album!!.title, style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold))
        }
        Row(modifier = Modifier.padding(10.dp, 3.dp)) {
            Text(album!!.artistId.toString(), style = TextStyle(fontSize = 14.sp))
        }
        Row(modifier = Modifier.padding(10.dp, 3.dp)) {
            Text(album!!.year, style = TextStyle(fontSize = 14.sp))
        }

        Row(
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth()
                .padding(0.dp, 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(10.dp, 0.dp)
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = "Play button")
            }
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .padding(10.dp, 0.dp)
            ) {
                Icon(Icons.Filled.PlayArrow, contentDescription = "Play button")
            }
        }

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}