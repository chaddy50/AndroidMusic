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
import com.chaddy50.musicapp.components.cards.EntityCard
import com.chaddy50.musicapp.components.TopBar
import com.chaddy50.musicapp.data.MusicDatabase
import com.chaddy50.musicapp.navigation.Screen
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

@Composable
fun Genres(
    viewModel: MusicAppViewModel,
    navController: NavController,
) {
    val genres by viewModel.genres.collectAsState()

    Scaffold(
        topBar = {
            TopBar(
                true,
                "Genres",
                navController
            )
        }
    ) { paddingValues ->
        LazyColumn(
            Modifier.padding(paddingValues)
        ) {
            items(items = genres, key = { it.id }) { genre ->
                EntityCard(
                    genre.name,
                    { navController.navigate(Screen.ArtistScreen.route + "?genreId=${genre.id}") }
                )
            }
        }
    }
}