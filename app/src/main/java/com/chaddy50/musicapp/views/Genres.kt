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
fun Genres(context: Context, musicDatabase: MusicDatabase, navController: NavController) {
    Scaffold(
        topBar = { TopBar("Genres") }
    ) {
        LazyColumn(
            modifier = Modifier.padding(it)
        ) {
            items(musicDatabase.genres.toList()) { genre ->
                EntityCard(
                    genre.title,
                    { navController.navigate(Screen.ArtistScreen.route + "?genreID=${genre.id}") }
                )
            }
        }
    }
}