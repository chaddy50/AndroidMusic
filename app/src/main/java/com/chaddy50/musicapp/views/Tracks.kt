package com.chaddy50.musicapp.views

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.chaddy50.musicapp.components.AlbumHeader
import com.chaddy50.musicapp.components.cards.EntityCard
import com.chaddy50.musicapp.components.TopBar
import com.chaddy50.musicapp.components.cards.TrackCard
import com.chaddy50.musicapp.data.MusicDatabase

@Composable
fun Tracks(
    context: Context,
    musicDatabase: MusicDatabase,
    navController: NavController,
    albumID: Int
) {
    val album = musicDatabase.albums.find {it.id == albumID}
    Scaffold(
        topBar = {
            TopBar(
                albumID != 0,
                album?.title ?: "Tracks",
                navController
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {

            if (albumID != 0) {
                AlbumHeader(album)
            }

            var tracksToShow = musicDatabase.tracks.toList()
            if (albumID != 0) {
                tracksToShow =
                    musicDatabase.tracks.filter { track -> track.albumID == albumID }
                tracksToShow = tracksToShow.sortedBy { it.number }
            }
            tracksToShow.forEach { track ->
                TrackCard(track)
            }
        }
    }
}