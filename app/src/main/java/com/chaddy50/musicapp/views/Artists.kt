package com.chaddy50.musicapp.views

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.chaddy50.musicapp.components.cards.EntityCard
import com.chaddy50.musicapp.components.TopBar
import com.chaddy50.musicapp.data.AlbumArtist
import com.chaddy50.musicapp.data.MusicDatabase
import com.chaddy50.musicapp.navigation.Screen

@Composable
fun Artists(
    context: Context,
    musicDatabase: MusicDatabase,
    navController: NavController,
    genreID: Int = 0
) {
    Scaffold(
        topBar = {
            TopBar(
                genreID != 0,
                musicDatabase.genres.find { it.id == genreID }?.title ?: "Artists",
                navController
            )
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it)) {
            var artistsToShow: List<AlbumArtist>
            if (genreID != 0) {
                artistsToShow = musicDatabase.albumArtists.filter { artist -> artist.genreID == genreID }
            } else {
                artistsToShow = musicDatabase.albumArtists.toList()
            }
            artistsToShow = artistsToShow.sortedBy { it.name }
            items(artistsToShow) { artist ->
                EntityCard(
                    artist.name,
                    { navController.navigate(Screen.AlbumScreen.route + "?artistName=${artist.name}") }
                )
            }
        }
    }
}