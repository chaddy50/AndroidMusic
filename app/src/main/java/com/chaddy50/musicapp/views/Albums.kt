package com.chaddy50.musicapp.views

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.chaddy50.musicapp.components.cards.AlbumCard
import com.chaddy50.musicapp.components.TopBar
import com.chaddy50.musicapp.data.Album
import com.chaddy50.musicapp.data.MusicDatabase

@Composable
fun Albums(
    context: Context,
    musicDatabase: MusicDatabase,
    navController: NavController,
    artistName: String = "",
) {
    Scaffold(
        topBar = {
            TopBar(
                artistName.isNotEmpty(),
                musicDatabase.albumArtists.find { it.name == artistName }?.name ?: "Albums",
                navController
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            var albumsToShow: List<Album>
            if (artistName.isNotEmpty()) {
                albumsToShow = musicDatabase.albums.filter { album -> album.artist == artistName }
            } else {
                albumsToShow = musicDatabase.albums.toList()
            }
            albumsToShow = albumsToShow.sortedBy { it.title }
            items(albumsToShow) { album ->
                AlbumCard(album, navController, artistName.isBlank())
            }
        }
    }
}