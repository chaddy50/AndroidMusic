package com.chaddy50.musicapp.features.genresScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.chaddy50.musicapp.features.artistsScreen.ArtistsScreen
import com.chaddy50.musicapp.navigation.MusicAppScreen
import com.chaddy50.musicapp.ui.composables.EntityCard
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
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

        EntityScreen(
            viewModel,
            navController,
            EntityType.All,
            uiState.screenTitle,
            uiState.isLoading,
            {
                uiState.genres.forEach { genre ->
                    EntityCard(
                        genre.name,
                        {
                            viewModel.updateSelectedGenre(genre.id)
                            navController.navigate(ArtistsScreen.route)
                        }
                    )
                }
            }
        )
    }
}