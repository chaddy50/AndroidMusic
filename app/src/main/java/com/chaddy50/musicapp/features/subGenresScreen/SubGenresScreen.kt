package com.chaddy50.musicapp.features.subGenresScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.chaddy50.musicapp.features.genresScreen.GenreList
import com.chaddy50.musicapp.features.genresScreen.rememberGenresScreenState
import com.chaddy50.musicapp.navigation.MusicAppScreen
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

object SubGenresScreen : MusicAppScreen {
    override val route = "sub_genres_screen"

    @Composable
    override fun Content(
        viewModel: MusicAppViewModel,
        navController: NavController,
        backStackEntry: NavBackStackEntry
    ) {
        val selectedGenreId = viewModel.selectedGenreId.collectAsStateWithLifecycle()
        val selectedAlbumArtistId = viewModel.selectedAlbumArtistId.collectAsStateWithLifecycle()

        val stateHolder = rememberSubGenresScreenState(
            selectedGenreId.value,
            selectedAlbumArtistId.value,
        )
        val uiState by stateHolder.uiState.collectAsStateWithLifecycle()

        SubGenreList(
            viewModel,
            navController,
            uiState,
        )
    }
}