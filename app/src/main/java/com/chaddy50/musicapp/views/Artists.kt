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
import com.chaddy50.musicapp.components.TopBar
import com.chaddy50.musicapp.components.cards.EntityCard
import com.chaddy50.musicapp.data.entity.AlbumArtist
import com.chaddy50.musicapp.navigation.Screen
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

@Composable
fun Artists(
    viewModel: MusicAppViewModel,
    navController: NavController,
    genreId: Int = 0
) {
    val allAlbumArtists by viewModel.albumArtists.collectAsState()
    val albumArtistsForGenre by viewModel.getAlbumArtistsForGenre(genreId).collectAsState()
    val genreName by viewModel.getGenreName(genreId).collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                true,
                genreName ?: "Artists",
                navController
            )
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            var artistsToShow: List<AlbumArtist>
            if (genreId != 0) {
                artistsToShow = albumArtistsForGenre
            } else {
                artistsToShow = allAlbumArtists
            }
            artistsToShow = artistsToShow.sortedBy { it.name }
            items(artistsToShow) { artist ->
                EntityCard(
                    artist.name,
                    { navController.navigate(Screen.AlbumScreen.route + "?artistId=${artist.id}") }
                )
            }
        }
    }
}