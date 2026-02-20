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
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.ui.screens.MusicAppScreen
import com.chaddy50.musicapp.ui.composables.AddToPlaylistSheet
import com.chaddy50.musicapp.ui.composables.CleanUpWhenNavigatingBackEffect
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityHeader
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
import com.chaddy50.musicapp.viewModel.MusicAppViewModel
import kotlinx.coroutines.flow.flowOf

object AlbumsScreen: MusicAppScreen {
    override val route = "albums_screen"

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
                viewModel.updateSelectedAlbumArtist(null)
                viewModel.updateSelectedSubGenre(null)
            }
        )

        val stateHolder = rememberAlbumsScreenState(viewModel)
        val uiState by stateHolder.uiState.collectAsStateWithLifecycle()
        val allPlaylists by viewModel.allPlaylists.collectAsStateWithLifecycle()

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
                        EntityHeader(viewModel, EntityType.AlbumArtist)
                    }

                    items(uiState.albums) { album ->
                        AlbumCard(
                            album,
                            viewModel,
                            navController,
                            onLongPress = { albumToAddToPlaylist = album },
                        )
                    }
                }

            },
            onPlay = { viewModel.playCurrentEntity(EntityType.AlbumArtist, false) },
            onShuffle = { viewModel.playCurrentEntity(EntityType.AlbumArtist, true) },
        )

        albumToAddToPlaylist?.let { album ->
            AddToPlaylistSheet(
                allPlaylists = allPlaylists,
                playlistsThatEntityIsAlreadyIn = playlistsThatAlbumIsAlreadyIn,
                onAddToPlaylist = { playlistId ->
                    viewModel.addEntityToPlaylistById(playlistId, EntityType.Album, album.id)
                },
                onCreateAndAdd = { name ->
                    viewModel.createPlaylistAndAddEntityById(name, EntityType.Album, album.id)
                },
                onDismiss = { albumToAddToPlaylist = null },
            )
        }
    }
}