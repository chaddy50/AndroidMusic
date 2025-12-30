package com.chaddy50.musicapp.features.artistsScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.chaddy50.musicapp.features.albumsScreen.AlbumsScreen
import com.chaddy50.musicapp.features.subGenresScreen.SubGenresScreen
import com.chaddy50.musicapp.navigation.MusicAppScreen
import com.chaddy50.musicapp.ui.composables.CleanUpWhenNavigatingBackEffect
import com.chaddy50.musicapp.ui.composables.EntityCard
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
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

        EntityScreen(
            viewModel,
            navController,
            EntityType.Genre,
            uiState.screenTitle,
            uiState.isLoading,
            {
                uiState.artists.forEach { artist ->
                    EntityCard(
                        artist.name,
                        {
                            viewModel.updateSelectedAlbumArtist(artist.id)
                            navController.navigate(AlbumsScreen.route)
                        }
                    )
                }
            }
        )
    }
}