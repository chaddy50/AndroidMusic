package com.chaddy50.musicapp.views

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.chaddy50.musicapp.components.cards.AlbumCard
import com.chaddy50.musicapp.components.TopBar
import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.data.MusicScanner
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

@Composable
fun Albums(
    viewModel: MusicAppViewModel,
    navController: NavController,
    artistId: Int = -1,
) {
    val allAlbums by viewModel.albums.collectAsState()
    val albumsForArtist by viewModel.getAlbumsForArtist(artistId).collectAsState()
    val albumArtistName by viewModel.getAlbumArtistName(artistId).collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                true,
                albumArtistName ?: "Albums",
                navController
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            var albumsToShow: List<Album>
            if (artistId != -1) {
                albumsToShow = albumsForArtist
            } else {
                albumsToShow = allAlbums
            }
            albumsToShow = albumsToShow.sortedBy { it.year }
            items(albumsToShow) { album ->
                AlbumCard(album, navController, artistId != -1)
            }
        }
    }
}