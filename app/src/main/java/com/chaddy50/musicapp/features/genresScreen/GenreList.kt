package com.chaddy50.musicapp.features.genresScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.chaddy50.musicapp.features.artistsScreen.ArtistsScreen
import com.chaddy50.musicapp.ui.composables.TopBar
import com.chaddy50.musicapp.ui.composables.EntityCard

@Composable
fun GenreList(
    navController: NavController,
    uiState: GenresScreenUiState,
) {
    Scaffold(
        topBar = {
            TopBar(
                true,
                "Genres",
                navController
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            CircularProgressIndicator()
        }
        else {
            LazyColumn(
                Modifier.padding(paddingValues)
            ) {
                items(uiState.genres) { genre ->
                    EntityCard(
                        genre.name,
                        { navController.navigate(ArtistsScreen.route + "?genreId=${genre.id}") }
                    )
                }
            }
        }
    }
}