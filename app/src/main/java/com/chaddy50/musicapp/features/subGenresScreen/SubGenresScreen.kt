package com.chaddy50.musicapp.features.subGenresScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.chaddy50.musicapp.features.albumsScreen.AlbumsScreen
import com.chaddy50.musicapp.navigation.MusicAppScreen
import com.chaddy50.musicapp.ui.composables.CleanUpWhenNavigatingBackEffect
import com.chaddy50.musicapp.ui.composables.EntityCard
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

object SubGenresScreen : MusicAppScreen {
    override val route = "sub_genres_screen"

    @Composable
    override fun Content(
        viewModel: MusicAppViewModel,
        navController: NavController,
        backStackEntry: NavBackStackEntry
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

        EntityScreen(
            viewModel,
            navController,
            EntityType.AlbumArtist,
            uiState.screenTitle,
            uiState.isLoading,
            {
                uiState.genres.forEach { genre ->
                    EntityCard(
                        genre.name,
                        {
                            viewModel.updateSelectedSubGenre(genre.id)
                            navController.navigate(AlbumsScreen.route)
                        }
                    )
                }
            }
        )
    }
}