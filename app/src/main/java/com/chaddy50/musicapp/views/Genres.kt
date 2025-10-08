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
import com.chaddy50.musicapp.data.MusicDatabase
import com.chaddy50.musicapp.navigation.Screen

@Composable
fun Genres(context: Context, musicDatabase: MusicDatabase, navController: NavController) {
    Scaffold(
        topBar = {
            TopBar(
                true,
                "Genres",
                navController
            )
        }
    ) {
        LazyColumn(
            Modifier.padding(it)
        ) {
            items(musicDatabase.genres.toList().sortedBy { it.title }) { genre ->
                EntityCard(
                    genre.title,
                    { navController.navigate(Screen.ArtistScreen.route + "?genreID=${genre.id}") }
                )
            }
        }
    }
}