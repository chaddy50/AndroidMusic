package com.chaddy50.musicapp.views

import android.content.Context
import android.provider.MediaStore
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.chaddy50.musicapp.components.EntityCard
import com.chaddy50.musicapp.components.TopBar
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
        topBar = { TopBar(genreID, musicDatabase.genres.find { it.id == genreID }?.title ?: "Artists") }
    ){
        LazyColumn(modifier = Modifier.padding(it)) {
            var artistsToShow = musicDatabase.artists.toList()
            if (genreID != 0) {
                artistsToShow = musicDatabase.artists.filter { artist -> artist.genreID == genreID }
            }
            items(artistsToShow) { artist ->
                EntityCard(
                    artist.name,
                    { navController.navigate(Screen.AlbumScreen.route + "?artistID=${artist.id}") }
                )
            }
        }
    }
}