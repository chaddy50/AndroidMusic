package com.chaddy50.musicapp.ui.screens.performancesScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chaddy50.musicapp.navigation.TracksRoute
import com.chaddy50.musicapp.ui.composables.EntityCard
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityHeader
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
import com.chaddy50.musicapp.ui.composables.nowPlayingBar.PlaybackViewModel
import com.chaddy50.musicapp.ui.screens.playlistsScreen.PlaylistViewModel

@Composable
fun PerformancesScreen(
    genreId: Long,
    albumId: Long,
    playbackViewModel: PlaybackViewModel,
    playlistViewModel: PlaylistViewModel,
    navController: NavController,
    screenViewModel: PerformancesScreenViewModel = hiltViewModel(),
) {
    val uiState by screenViewModel.uiState.collectAsStateWithLifecycle()
    val entityHeaderState by screenViewModel.entityHeaderState.collectAsStateWithLifecycle()
    val allPlaylists by playlistViewModel.allPlaylists.collectAsStateWithLifecycle()

    EntityScreen(
        uiState.isLoading,
        {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    EntityHeader(
                        uiState = entityHeaderState,
                        type = EntityType.Album,
                        allPlaylists = allPlaylists,
                        onAddToPlaylist = { playlistId -> playlistViewModel.addAlbumToPlaylist(playlistId, albumId) },
                        onCreateAndAdd = { name -> playlistViewModel.createPlaylistAndAddAlbum(name, albumId) },
                    )
                }

                items(uiState.performances) { performance ->
                    EntityCard(
                        title = performance.artistName,
                        onClick = {
                            navController.navigate(TracksRoute(genreId = genreId, albumId = albumId, performanceId = performance.id, title = uiState.screenTitle))
                        },
                        subtitle = performance.year,
                    )
                }
            }
        },
        onPlay = if (uiState.performances.isNotEmpty()) {{ playbackViewModel.playTracksForAlbumInGenre(albumId, screenViewModel.genreId, null, false) }} else null,
        onShuffle = if (uiState.performances.isNotEmpty()) {{ playbackViewModel.playTracksForAlbumInGenre(albumId, screenViewModel.genreId, null, true) }} else null,
    )
}
