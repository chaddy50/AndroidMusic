package com.chaddy50.musicapp.features.albumsScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.chaddy50.musicapp.navigation.MusicAppScreen
import com.chaddy50.musicapp.ui.composables.CleanUpWhenNavigatingBackEffect
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

object AlbumsScreen: MusicAppScreen {
    override val route = "albums_screen"

    @Composable
    override fun Content(
        viewModel: MusicAppViewModel,
        navController: NavController,
        backStackEntry: NavBackStackEntry
    ) {
        val selectedSubGenreId by viewModel.selectedSubGenreId.collectAsStateWithLifecycle()

        CleanUpWhenNavigatingBackEffect(
            navController,
            route,
            {
                if (selectedSubGenreId != null) {
                    viewModel.updateSelectedSubGenre(null)
                } else {
                    viewModel.updateSelectedAlbumArtist(null)
                }
            }
        )

        val stateHolder = rememberAlbumsScreenState(viewModel)
        val uiState by stateHolder.uiState.collectAsStateWithLifecycle()

        EntityScreen(
            viewModel,
            navController,
            EntityType.AlbumArtist,
            uiState.screenTitle,
            uiState.isLoading,
            {
                uiState.albums.forEach { album ->
                    AlbumCard(
                        album,
                        viewModel,
                        navController,
                    )
                }
            }
        )
    }
}