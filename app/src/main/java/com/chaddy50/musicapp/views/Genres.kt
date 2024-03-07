package com.chaddy50.musicapp.views

import android.content.Context
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.chaddy50.musicapp.components.EntityCard
import com.chaddy50.musicapp.data.MusicDatabase
import com.chaddy50.musicapp.navigation.Screen

@Composable
fun Genres(context: Context, musicDatabase: MusicDatabase, navController: NavController) {
    LazyColumn {
        items(musicDatabase.genres.toList()) { genre ->
            EntityCard(
                genre.title,
                { navController.navigate(Screen.ArtistScreen.route + "?genreID=${genre.id}") }
            )
        }
    }
}