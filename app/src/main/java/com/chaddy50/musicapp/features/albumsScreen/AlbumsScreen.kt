package com.chaddy50.musicapp.features.albumsScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.chaddy50.musicapp.navigation.MusicAppScreen
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

object AlbumsScreen: MusicAppScreen {
    override val route = "albums_screen"

    @Composable
    override fun Content(
        viewModel: MusicAppViewModel,
        navController: NavController,
        backStackEntry: NavBackStackEntry
    ) {
        val selectedAlbumArtistId = viewModel.selectedAlbumArtistId.collectAsStateWithLifecycle()
        val selectedSubGenreId = viewModel.selectedSubGenreId.collectAsStateWithLifecycle()

        val stateHolder = rememberAlbumsScreenState(
            selectedAlbumArtistId.value,
            selectedSubGenreId.value
        )
        val uiState by stateHolder.uiState.collectAsStateWithLifecycle()

        AlbumList(
            viewModel,
            navController,
            uiState
        )
    }
}