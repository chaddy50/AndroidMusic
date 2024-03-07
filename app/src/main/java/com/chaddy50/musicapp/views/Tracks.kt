package com.chaddy50.musicapp.views

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.chaddy50.musicapp.components.EntityCard
import com.chaddy50.musicapp.components.TopBar
import com.chaddy50.musicapp.data.MusicDatabase

@Composable
fun Tracks(
    context: Context,
    musicDatabase: MusicDatabase,
    navController: NavController,
    albumID: Int
) {
    Scaffold(
        topBar = { TopBar(albumID, musicDatabase.albums.find { it.id == albumID }?.title ?: "Tracks") }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            var tracksToShow = musicDatabase.tracks.toList()
            if (albumID != 0) {
                tracksToShow = musicDatabase.tracks.filter { track -> track.albumID == albumID }
            }
            items(tracksToShow) {track ->
                EntityCard(track.title , {})
            }
        }
    }
}