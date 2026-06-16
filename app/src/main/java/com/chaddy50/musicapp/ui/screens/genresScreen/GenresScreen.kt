package com.chaddy50.musicapp.ui.screens.genresScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chaddy50.musicapp.data.entity.Genre
import com.chaddy50.musicapp.navigation.ArtistsRoute
import com.chaddy50.musicapp.ui.composables.AddToPlaylistHandler
import com.chaddy50.musicapp.ui.composables.EmptyStateContent
import com.chaddy50.musicapp.ui.composables.EntityCard
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.ui.composables.nowPlayingBar.PlaybackViewModel
import com.chaddy50.musicapp.ui.composables.rememberAddToPlaylistState
import com.chaddy50.musicapp.ui.screens.playlistsScreen.PlaylistViewModel

@Composable
fun GenresScreen(
    playbackViewModel: PlaybackViewModel,
    playlistViewModel: PlaylistViewModel,
    navController: NavController,
    screenViewModel: GenresScreenViewModel = hiltViewModel(),
) {
    val uiState by screenViewModel.uiState.collectAsStateWithLifecycle()
    val allPlaylists by playlistViewModel.allPlaylists.collectAsStateWithLifecycle()

    val addToPlaylistState = rememberAddToPlaylistState<Genre>(
        getPlaylistMembership = { genre -> playlistViewModel.getPlaylistsThatGenreIsAlreadyIn(genre.id) },
        onAdd = { playlistId, genre -> playlistViewModel.addGenreToPlaylist(playlistId, genre.id) },
        onCreateAndAdd = { name, genre -> playlistViewModel.createPlaylistAndAddGenre(name, genre.id) },
    )

    EntityScreen(
        uiState.isLoading,
        {
            if (uiState.genres.isEmpty()) {
                EmptyStateContent(
                    icon = Icons.Filled.LibraryMusic,
                    title = "No music yet",
                    subtitle = "Add music to your device to get started",
                )
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.genres) { genreWithStats ->
                        EntityCard(
                            genreWithStats.genre.name,
                            onClick = {
                                navController.navigate(
                                    ArtistsRoute(
                                        genreId = genreWithStats.genre.id,
                                        title = genreWithStats.genre.name,
                                    )
                                )
                            },
                            onLongClick = { addToPlaylistState.show(genreWithStats.genre) },
                            icon = genreIcon(genreWithStats.genre.name),
                            subtitle = genreWithStats.subtitle,
                        )
                    }
                }
            }
        },
        onPlay = { playbackViewModel.playAllTracks(false) },
        onShuffle = { playbackViewModel.playAllTracks(true) },
    )

    AddToPlaylistHandler(state = addToPlaylistState, allPlaylists = allPlaylists)
}
