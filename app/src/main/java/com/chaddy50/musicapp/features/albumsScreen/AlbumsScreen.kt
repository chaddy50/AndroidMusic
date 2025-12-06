package com.chaddy50.musicapp.features.albumsScreen

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.chaddy50.musicapp.navigation.MusicAppScreen
import com.chaddy50.musicapp.ui.composables.CleanUpWhenNavigatingBackEffect
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

object AlbumsScreen: MusicAppScreen {
    override val route = "albums_screen"

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
                Log.d("NPC","This is a message: ${viewModel.selectedAlbumArtistId.value}")
                if (viewModel.selectedSubGenreId.value != null) {
                    viewModel.updateSelectedSubGenre(null)
                } else {
                    viewModel.updateSelectedAlbumArtist(null)
                }
            }
        )

        val stateHolder = rememberAlbumsScreenState(viewModel)
        val uiState by stateHolder.uiState.collectAsStateWithLifecycle()

        AlbumList(
            viewModel,
            navController,
            uiState
        )
    }
}