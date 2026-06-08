package com.chaddy50.musicapp.ui.screens.artistsScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chaddy50.musicapp.data.entity.AlbumArtist
import com.chaddy50.musicapp.navigation.AlbumsRoute
import com.chaddy50.musicapp.ui.composables.AddToPlaylistHandler
import com.chaddy50.musicapp.ui.composables.EntityCard
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityHeader
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
import com.chaddy50.musicapp.ui.composables.nowPlayingBar.PlaybackViewModel
import com.chaddy50.musicapp.ui.composables.rememberAddToPlaylistState
import com.chaddy50.musicapp.ui.screens.playlistsScreen.PlaylistViewModel

@Composable
fun ArtistsScreen(
    genreId: Long,
    playbackViewModel: PlaybackViewModel,
    playlistViewModel: PlaylistViewModel,
    navController: NavController,
    screenViewModel: ArtistsScreenViewModel = hiltViewModel(),
) {
    val uiState by screenViewModel.uiState.collectAsStateWithLifecycle()
    val entityHeaderState by screenViewModel.entityHeaderState.collectAsStateWithLifecycle()
    val allPlaylists by playlistViewModel.allPlaylists.collectAsStateWithLifecycle()

    val addToPlaylistState = rememberAddToPlaylistState<AlbumArtist>(
        getPlaylistMembership = { artist -> playlistViewModel.getPlaylistsThatAlbumArtistIsAlreadyIn(artist.id) },
        onAdd = { playlistId, artist -> playlistViewModel.addAlbumArtistToPlaylist(playlistId, artist.id) },
        onCreateAndAdd = { name, artist -> playlistViewModel.createPlaylistAndAddAlbumArtist(name, artist.id) },
    )

    EntityScreen(
        uiState.isLoading,
        {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    EntityHeader(
                        uiState = entityHeaderState,
                        type = EntityType.Genre,
                        allPlaylists = allPlaylists,
                        onAddToPlaylist = { playlistId -> playlistViewModel.addGenreToPlaylist(playlistId, genreId) },
                        onCreateAndAdd = { name -> playlistViewModel.createPlaylistAndAddGenre(name, genreId) },
                    )
                }

                items(uiState.artists) { artist ->
                    EntityCard(
                        artist.name,
                        onClick = {
                            navController.navigate(AlbumsRoute(genreId = genreId, albumArtistId = artist.id, title = artist.name))
                        },
                        onLongClick = { addToPlaylistState.show(artist) },
                    )
                }
            }

        },
        onPlay = { playbackViewModel.playTracksForGenre(genreId, false) },
        onShuffle = { playbackViewModel.playTracksForGenre(genreId, true) },
    )

    AddToPlaylistHandler(state = addToPlaylistState, allPlaylists = allPlaylists)
}
