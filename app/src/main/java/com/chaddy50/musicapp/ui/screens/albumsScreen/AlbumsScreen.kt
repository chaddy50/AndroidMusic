package com.chaddy50.musicapp.ui.screens.albumsScreen

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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.navigation.PerformancesRoute
import com.chaddy50.musicapp.navigation.TracksRoute
import com.chaddy50.musicapp.ui.composables.AddToPlaylistSheet
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityHeader
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
import com.chaddy50.musicapp.viewModel.MusicAppViewModel
import kotlinx.coroutines.flow.flowOf

@Composable
fun AlbumsScreen(
    genreId: Long,
    albumArtistId: Long,
    viewModel: MusicAppViewModel,
    navController: NavController,
    onTitleChanged: (String) -> Unit,
) {
    val stateHolder = rememberAlbumsScreenState(genreId, albumArtistId, viewModel)
    val uiState by stateHolder.uiState.collectAsStateWithLifecycle()
    val allPlaylists by viewModel.allPlaylists.collectAsStateWithLifecycle()
    val isClassical = genreId == viewModel.classicalGenreId
    val selectedSubGenreId by viewModel.selectedSubGenreId.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.screenTitle, uiState.isLoading) {
        if (!uiState.isLoading) {
            onTitleChanged(uiState.screenTitle)
        }
    }

    var albumToAddToPlaylist by remember { mutableStateOf<Album?>(null) }
    val playlistsThatAlbumIsAlreadyIn by remember(albumToAddToPlaylist?.id) {
        albumToAddToPlaylist?.let { viewModel.getPlaylistsThatAlbumIsAlreadyIn(it.id) } ?: flowOf(emptySet())
    }.collectAsStateWithLifecycle(emptySet())

    EntityScreen(
        uiState.isLoading,
        {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    EntityHeader(
                        type = EntityType.AlbumArtist,
                        genreId = genreId,
                        albumArtistId = albumArtistId,
                        classicalGenreId = viewModel.classicalGenreId,
                        allPlaylists = allPlaylists,
                        onAddToPlaylist = { playlistId -> viewModel.addAlbumArtistToPlaylist(playlistId, albumArtistId) },
                        onCreateAndAdd = { name -> viewModel.createPlaylistAndAddAlbumArtist(name, albumArtistId) },
                    )
                }

                items(uiState.albums) { album ->
                    AlbumCard(
                        album = album,
                        isClassical = isClassical,
                        onAlbumClick = {
                            if (isClassical) {
                                navController.navigate(PerformancesRoute(genreId = genreId, albumId = album.id))
                            } else {
                                navController.navigate(TracksRoute(genreId = genreId, albumId = album.id))
                            }
                        },
                        onLongPress = { albumToAddToPlaylist = album },
                    )
                }
            }

        },
        onPlay = { viewModel.playTracksForAlbumArtist(albumArtistId, selectedSubGenreId, false) },
        onShuffle = { viewModel.playTracksForAlbumArtist(albumArtistId, selectedSubGenreId, true) },
    )

    albumToAddToPlaylist?.let { album ->
        AddToPlaylistSheet(
            allPlaylists = allPlaylists,
            playlistsThatEntityIsAlreadyIn = playlistsThatAlbumIsAlreadyIn,
            onAddToPlaylist = { playlistId ->
                viewModel.addAlbumToPlaylist(playlistId, album.id)
            },
            onCreateAndAdd = { name ->
                viewModel.createPlaylistAndAddAlbum(name, album.id)
            },
            onDismiss = { albumToAddToPlaylist = null },
        )
    }
}
