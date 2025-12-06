package com.chaddy50.musicapp.features.subGenresScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.chaddy50.musicapp.features.albumsScreen.AlbumsScreen
import com.chaddy50.musicapp.features.genresScreen.SubGenresScreenUiState
import com.chaddy50.musicapp.ui.composables.EntityCard
import com.chaddy50.musicapp.ui.composables.TopBar
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

@Composable
fun SubGenreList(
    viewModel: MusicAppViewModel,
    navController: NavController,
    uiState: SubGenresScreenUiState,
) {
    Scaffold(
        topBar = {
            TopBar(
                true,
                uiState.screenTitle,
                navController
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn(Modifier.padding(paddingValues)) {
                items(uiState.genres) { genre ->
                    EntityCard(
                        genre.name,
                        {
                            viewModel.updateSelectedSubGenre(genre.id)
                            navController.navigate(AlbumsScreen.route)
                        }
                    )
                }
            }
        }
    }
}