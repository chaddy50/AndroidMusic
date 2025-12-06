package com.chaddy50.musicapp.features.artistsScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.chaddy50.musicapp.navigation.MusicAppScreen
import com.chaddy50.musicapp.ui.composables.CleanUpWhenNavigatingBackEffect
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

object ArtistsScreen: MusicAppScreen {
    override val route = "artists_screen"

    @Composable
    override fun Content(
        viewModel: MusicAppViewModel,
        navController: NavController,
        backStackEntry: NavBackStackEntry
    ) {
        CleanUpWhenNavigatingBackEffect(
            navController,
            route,
            { viewModel.updateSelectedGenre(null) }
        )

        val selectedGenreId = viewModel.selectedGenreId.collectAsStateWithLifecycle()
        val stateHolder = rememberArtistsScreenState(selectedGenreId.value)
        val uiState by stateHolder.uiState.collectAsStateWithLifecycle()

        ArtistList(
            viewModel,
            navController,
            uiState
        )
    }
}