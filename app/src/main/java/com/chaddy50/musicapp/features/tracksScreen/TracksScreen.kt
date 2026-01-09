package com.chaddy50.musicapp.features.tracksScreen

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

object TracksScreen: MusicAppScreen {
    override val route = "tracks_screen"

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

        EntityScreen(
            viewModel,
            navController,
            EntityType.Album,
            uiState.screenTitle,
            uiState.isLoading,
            {
                uiState.tracks.forEach { track ->
                    TrackCard(
                        track,
                        { viewModel.playTrack(track)}
                    )
                }
            }
        )
    }
}