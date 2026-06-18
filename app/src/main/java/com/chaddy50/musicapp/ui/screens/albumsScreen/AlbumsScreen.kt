package com.chaddy50.musicapp.ui.screens.albumsScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.navigation.PerformancesRoute
import com.chaddy50.musicapp.navigation.TracksRoute
import com.chaddy50.musicapp.ui.composables.AddToPlaylistHandler
import com.chaddy50.musicapp.ui.composables.EntityCard
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityHeader
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
import com.chaddy50.musicapp.ui.composables.nowPlayingBar.PlaybackViewModel
import com.chaddy50.musicapp.ui.composables.rememberAddToPlaylistState
import com.chaddy50.musicapp.ui.screens.playlistsScreen.PlaylistViewModel

@Composable
fun AlbumsScreen(
    genreId: Long,
    albumArtistId: Long,
    playbackViewModel: PlaybackViewModel,
    playlistViewModel: PlaylistViewModel,
    navController: NavController,
    screenViewModel: AlbumsScreenViewModel = hiltViewModel(),
) {
    val uiState by screenViewModel.uiState.collectAsStateWithLifecycle()
    val entityHeaderState by screenViewModel.entityHeaderState.collectAsStateWithLifecycle()
    val allPlaylists by playlistViewModel.allPlaylists.collectAsStateWithLifecycle()
    val isClassical = screenViewModel.isClassical
    val selectedSubGenreId by screenViewModel.selectedSubGenreId.collectAsStateWithLifecycle()
    val effectiveGenreId = selectedSubGenreId ?: screenViewModel.genreId

    val addToPlaylistState = rememberAddToPlaylistState<Album>(
        getPlaylistMembership = { album -> playlistViewModel.getPlaylistsThatAlbumIsAlreadyIn(album.id) },
        onAdd = { playlistId, album -> playlistViewModel.addAlbumToPlaylist(playlistId, album.id) },
        onCreateAndAdd = { name, album -> playlistViewModel.createPlaylistAndAddAlbum(name, album.id) },
    )

    EntityScreen(
        uiState.isLoading,
        {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    EntityHeader(
                        uiState = entityHeaderState,
                        type = EntityType.AlbumArtist,
                        allPlaylists = allPlaylists,
                        onAddToPlaylist = { playlistId -> playlistViewModel.addAlbumArtistToPlaylist(playlistId, albumArtistId) },
                        onCreateAndAdd = { name -> playlistViewModel.createPlaylistAndAddAlbumArtist(name, albumArtistId) },
                    )
                }

                items(uiState.albums) { album ->
                    EntityCard(
                        title = album.title,
                        onClick = {
                            if (isClassical) {
                                navController.navigate(PerformancesRoute(genreId = genreId, albumId = album.id, title = album.title))
                            } else {
                                navController.navigate(TracksRoute(genreId = genreId, albumId = album.id, title = album.title))
                            }
                        },
                        onLongClick = { addToPlaylistState.show(album) },
                        artworkPath = if (!isClassical) album.artworkPath else null,
                        subtitle = if (isClassical) album.catalogueString else album.year,
                    )
                }
            }

        },
        onPlay = if (uiState.albums.isNotEmpty()) {{ playbackViewModel.playTracksForAlbumArtist(albumArtistId, effectiveGenreId, false) }} else null,
        onShuffle = if (uiState.albums.isNotEmpty()) {{ playbackViewModel.playTracksForAlbumArtist(albumArtistId, effectiveGenreId, true) }} else null,
    )

    AddToPlaylistHandler(state = addToPlaylistState, allPlaylists = allPlaylists)
}
