package com.chaddy50.musicapp.ui.screens.performancesScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chaddy50.musicapp.navigation.TracksRoute
import com.chaddy50.musicapp.ui.composables.EntityCard
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityHeader
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

@Composable
fun PerformancesScreen(
    genreId: Long,
    albumId: Long,
    viewModel: MusicAppViewModel,
    navController: NavController,
    onTitleChanged: (String) -> Unit,
) {
    val stateHolder = rememberPerformancesScreenState(albumId, null)
    val uiState by stateHolder.uiState.collectAsStateWithLifecycle()
    val allPlaylists by viewModel.allPlaylists.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.screenTitle, uiState.isLoading) {
        if (!uiState.isLoading) {
            onTitleChanged(uiState.screenTitle)
        }
    }

    EntityScreen(
        uiState.isLoading,
        {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    EntityHeader(
                        type = EntityType.Album,
                        genreId = genreId,
                        albumId = albumId,
                        classicalGenreId = viewModel.classicalGenreId,
                        allPlaylists = allPlaylists,
                        onAddToPlaylist = { playlistId -> viewModel.addAlbumToPlaylist(playlistId, albumId) },
                        onCreateAndAdd = { name -> viewModel.createPlaylistAndAddAlbum(name, albumId) },
                    )
                }

                items(uiState.performances) { performance ->
                    EntityCard(
                        "${performance.year} - ${performance.artistName}",
                        {
                            navController.navigate(TracksRoute(genreId = genreId, albumId = albumId, performanceId = performance.id))
                        }
                    )
                }
            }
        },
        onPlay = { viewModel.playTracksForAlbum(albumId, null, false) },
        onShuffle = { viewModel.playTracksForAlbum(albumId, null, true) },
    )
}
