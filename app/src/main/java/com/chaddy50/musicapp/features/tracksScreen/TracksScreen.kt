package com.chaddy50.musicapp.features.tracksScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

        val doesAlbumHaveMultipleDiscs = if (uiState.tracks.isNotEmpty()) uiState.tracks.first().discNumber != uiState.tracks.last().discNumber else false
        var currentDiscNumber = 0

        EntityScreen(
            viewModel,
            navController,
            EntityType.Album,
            uiState.screenTitle,
            uiState.isLoading,
            {
                uiState.tracks.forEach { track ->
                    if (doesAlbumHaveMultipleDiscs && (track.discNumber > 0) && (track.discNumber != currentDiscNumber)) {
                        currentDiscNumber = track.discNumber
                        Text(
                            "Disc $currentDiscNumber",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    TrackCard(
                        track,
                        { viewModel.playTrack(track)}
                    )
                }
            }
        )
    }
}