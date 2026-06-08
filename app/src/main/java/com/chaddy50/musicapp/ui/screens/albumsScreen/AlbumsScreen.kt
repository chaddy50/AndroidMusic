package com.chaddy50.musicapp.ui.screens.albumsScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chaddy50.musicapp.MusicApplication
import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.navigation.PerformancesRoute
import com.chaddy50.musicapp.navigation.TracksRoute
import com.chaddy50.musicapp.ui.composables.AddToPlaylistHandler
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
    val app = LocalContext.current.applicationContext as MusicApplication
    val uiState by screenViewModel.uiState.collectAsStateWithLifecycle()
    val entityHeaderState by screenViewModel.entityHeaderState.collectAsStateWithLifecycle()
    val allPlaylists by playlistViewModel.allPlaylists.collectAsStateWithLifecycle()
    val isClassical = genreId == app.classicalGenreId
    val selectedSubGenreId by screenViewModel.selectedSubGenreId.collectAsStateWithLifecycle()

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
                    AlbumCard(
                        album = album,
                        isClassical = isClassical,
                        onAlbumClick = {
                            if (isClassical) {
                                navController.navigate(PerformancesRoute(genreId = genreId, albumId = album.id, title = album.title))
                            } else {
                                navController.navigate(TracksRoute(genreId = genreId, albumId = album.id, title = album.title))
                            }
                        },
                        onLongPress = { addToPlaylistState.show(album) },
                    )
                }
            }

        },
        onPlay = { playbackViewModel.playTracksForAlbumArtist(albumArtistId, selectedSubGenreId, false) },
        onShuffle = { playbackViewModel.playTracksForAlbumArtist(albumArtistId, selectedSubGenreId, true) },
    )

    AddToPlaylistHandler(state = addToPlaylistState, allPlaylists = allPlaylists)
}
