package com.chaddy50.musicapp.features.tracksScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.chaddy50.musicapp.navigation.MusicAppScreen
import com.chaddy50.musicapp.ui.composables.CleanUpWhenNavigatingBackEffect
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

object TracksScreen: MusicAppScreen {
    override val route = "tracks_screen"
    const val ARG_ALBUM_ID = "albumId"

    override val arguments = listOf(
        navArgument(ARG_ALBUM_ID) {
            type = NavType.IntType
            defaultValue = 0
        }
    )

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
                if (viewModel.selectedPerformanceId.value != null) {
                    viewModel.updateSelectedPerformance(null)
                } else {
                    viewModel.updateSelectedAlbum(null)
                }
            }
        )

        val albumId = viewModel.selectedAlbumId.collectAsStateWithLifecycle()
        val performanceId = viewModel.selectedPerformanceId.collectAsStateWithLifecycle()
        val stateHolder = rememberTracksScreenState(
            albumId.value,
            performanceId.value
        )
        val uiState by stateHolder.uiState.collectAsStateWithLifecycle()

        TrackList(navController, uiState)
    }
}