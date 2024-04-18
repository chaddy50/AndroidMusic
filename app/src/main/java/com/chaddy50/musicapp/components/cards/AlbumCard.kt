package com.chaddy50.musicapp.components.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chaddy50.musicapp.data.Album
import com.chaddy50.musicapp.navigation.Screen

@Composable
fun AlbumCard(
    album: Album,
    navController: NavController,
    shouldShowArtist: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(4.dp)
            .clickable { navController.navigate(Screen.TrackScreen.route + "?albumID=${album.id}") }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column{
                    album.artwork?.asImageBitmap()
                        ?.let { Image(bitmap = it, contentDescription = "Album artwork") }
                }
                Column(modifier = Modifier.padding(10.dp, 0.dp)) {
                    Row {
                        Text(album.title, style = TextStyle(fontSize = 16.sp))
                    }
                    if (shouldShowArtist) {
                        Row {
                            Text(album.artist, style= TextStyle(fontSize = 14.sp))
                        }
                    }
                }
            }
        }
    }
}