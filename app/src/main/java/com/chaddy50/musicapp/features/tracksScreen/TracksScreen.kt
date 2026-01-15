package com.chaddy50.musicapp.features.tracksScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityHeader
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

object TracksScreen: MusicAppScreen {
    override val route = "tracks_screen"

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content(
        viewModel: MusicAppViewModel,
        navController: NavController,
        backStackEntry: NavBackStackEntry,
        onTitleChanged: (title: String) -> Unit,
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

        LaunchedEffect(uiState.screenTitle, uiState.isLoading) {
            if (!uiState.isLoading) {
                onTitleChanged(uiState.screenTitle)
            }
        }

        val groupedTracks = uiState.tracks.groupBy { it.discNumber }
        val doesAlbumHaveMultipleDiscs = groupedTracks.size > 1

        EntityScreen(
            uiState.isLoading,
            {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        EntityHeader(viewModel, EntityType.Album)
                    }

                    groupedTracks.forEach { (discNumber, tracks) ->
                        if (doesAlbumHaveMultipleDiscs && (discNumber > 0)) {
                            stickyHeader {
                                Text(
                                    "Disc $discNumber",
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                        items(tracks) { track ->
                            TrackCard(
                                track,
                                { viewModel.playTrack(track) }
                            )
                        }
                     }
                }
            }
        )
    }
}