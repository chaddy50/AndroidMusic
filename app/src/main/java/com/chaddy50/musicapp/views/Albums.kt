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
import com.chaddy50.musicapp.navigation.Screen

@Composable
fun Albums(
    context: Context,
    musicDatabase: MusicDatabase,
    navController: NavController,
    artistID: Int = 0
) {
    Scaffold(
        topBar = {
            TopBar(
                artistID != 0,
                musicDatabase.artists.find { it.id == artistID }?.name ?: "Albums",
                navController
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            var albumsToShow = musicDatabase.albums.toList()
            if (artistID != 0) {
                albumsToShow = musicDatabase.albums.filter { album -> album.artistID == artistID }
            }
            items(albumsToShow) { album ->
                EntityCard(
                    album.title,
                    { navController.navigate(Screen.TrackScreen.route + "?albumID=${album.id}") }
                )
            }
        }
    }
}