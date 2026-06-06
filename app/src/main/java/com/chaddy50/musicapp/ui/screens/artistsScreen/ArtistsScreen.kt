package com.chaddy50.musicapp.ui.screens.artistsScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chaddy50.musicapp.data.entity.AlbumArtist
import com.chaddy50.musicapp.navigation.AlbumsRoute
import com.chaddy50.musicapp.ui.composables.AddToPlaylistSheet
import com.chaddy50.musicapp.ui.composables.EntityCard
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityHeader
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
import com.chaddy50.musicapp.ui.composables.nowPlayingBar.PlaybackViewModel
import com.chaddy50.musicapp.ui.screens.playlistsScreen.PlaylistViewModel
import kotlinx.coroutines.flow.flowOf

@Composable
fun ArtistsScreen(
    genreId: Long,
    playbackViewModel: PlaybackViewModel,
    playlistViewModel: PlaylistViewModel,
    navController: NavController,
    onTitleChanged: (String) -> Unit,
    screenViewModel: ArtistsScreenViewModel = hiltViewModel(),
) {
    val uiState by screenViewModel.uiState.collectAsStateWithLifecycle()
    val entityHeaderState by screenViewModel.entityHeaderState.collectAsStateWithLifecycle()
    val allPlaylists by playlistViewModel.allPlaylists.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.screenTitle, uiState.isLoading) {
        if (!uiState.isLoading) {
            onTitleChanged(uiState.screenTitle)
        }
    }

    var artistToAddToPlaylist by remember { mutableStateOf<AlbumArtist?>(null) }
    val playlistsThatArtistIsAlreadyIn by remember(artistToAddToPlaylist?.id) {
        artistToAddToPlaylist?.let { playlistViewModel.getPlaylistsThatAlbumArtistIsAlreadyIn(it.id) } ?: flowOf(emptySet())
    }.collectAsStateWithLifecycle(emptySet())

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
                            navController.navigate(AlbumsRoute(genreId = genreId, albumArtistId = artist.id))
                        },
                        onLongClick = { artistToAddToPlaylist = artist },
                    )
                }
            }

        },
        onPlay = { playbackViewModel.playTracksForGenre(genreId, false) },
        onShuffle = { playbackViewModel.playTracksForGenre(genreId, true) },
    )

    artistToAddToPlaylist?.let { artist ->
        AddToPlaylistSheet(
            allPlaylists = allPlaylists,
            playlistsThatEntityIsAlreadyIn = playlistsThatArtistIsAlreadyIn,
            onAddToPlaylist = { playlistId ->
                playlistViewModel.addAlbumArtistToPlaylist(playlistId, artist.id)
            },
            onCreateAndAdd = { name ->
                playlistViewModel.createPlaylistAndAddAlbumArtist(name, artist.id)
            },
            onDismiss = { artistToAddToPlaylist = null },
        )
    }
}
