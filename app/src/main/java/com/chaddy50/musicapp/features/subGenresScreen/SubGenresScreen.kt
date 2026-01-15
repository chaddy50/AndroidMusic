package com.chaddy50.musicapp.features.subGenresScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.chaddy50.musicapp.features.albumsScreen.AlbumsScreen
import com.chaddy50.musicapp.navigation.MusicAppScreen
import com.chaddy50.musicapp.ui.composables.CleanUpWhenNavigatingBackEffect
import com.chaddy50.musicapp.ui.composables.EntityCard
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

object SubGenresScreen : MusicAppScreen {
    override val route = "sub_genres_screen"

    @Composable
    override fun Content(
        viewModel: MusicAppViewModel,
        navController: NavController,
        backStackEntry: NavBackStackEntry,
        onTitleChanged: (title: String) -> Unit,
    ) {
        CleanUpWhenNavigatingBackEffect(
            navController,
            route,
            {
                viewModel.updateSelectedAlbumArtist(null)
            }
        )

        val selectedGenreId = viewModel.selectedGenreId.collectAsStateWithLifecycle()
        val selectedAlbumArtistId = viewModel.selectedAlbumArtistId.collectAsStateWithLifecycle()

        val stateHolder = rememberSubGenresScreenState(
            selectedGenreId.value,
            selectedAlbumArtistId.value,
        )
        val uiState by stateHolder.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(uiState.screenTitle, uiState.isLoading) {
            if (!uiState.isLoading) {
                onTitleChanged(uiState.screenTitle)
            }
        }

        EntityScreen(
            uiState.isLoading,
            {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
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
        )
    }
}