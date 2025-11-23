package com.chaddy50.musicapp.views

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.chaddy50.musicapp.components.AlbumHeader
import com.chaddy50.musicapp.components.TopBar
import com.chaddy50.musicapp.components.cards.TrackCard
import com.chaddy50.musicapp.data.MusicScanner
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

@Composable
fun Tracks(
    viewModel: MusicAppViewModel,
    navController: NavController,
    albumId: Int
) {
    val tracksForAlbum by viewModel.getTracksForAlbum(albumId).collectAsState()
    val albumName by viewModel.getAlbumName(albumId).collectAsState()
    Scaffold(
        topBar = {
            TopBar(
                albumId != 0,
                albumName ?: "Tracks",
                navController
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {

//            if (albumId != 0) {
//                AlbumHeader(album)
//            }

            tracksForAlbum.forEach { track ->
                TrackCard(track)
            }
        }
    }
}