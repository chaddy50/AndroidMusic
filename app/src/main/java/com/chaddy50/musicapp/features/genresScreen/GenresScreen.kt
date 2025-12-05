package com.chaddy50.musicapp.features.genresScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.chaddy50.musicapp.navigation.MusicAppScreen
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

object GenresScreen : MusicAppScreen {
    override val route = "genres_screen"

    @Composable
    override fun Content(
        viewModel: MusicAppViewModel,
        navController: NavController,
        backStackEntry: NavBackStackEntry
    ) {
        val stateHolder = rememberGenresScreenState()
        val uiState by stateHolder.uiState.collectAsStateWithLifecycle()

        GenreList(
            viewModel,
            navController,
            uiState
        )
    }
}