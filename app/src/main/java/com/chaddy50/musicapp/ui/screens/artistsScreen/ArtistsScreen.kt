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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.chaddy50.musicapp.data.entity.AlbumArtist
import com.chaddy50.musicapp.ui.screens.albumsScreen.AlbumsScreen
import com.chaddy50.musicapp.ui.screens.MusicAppScreen
import com.chaddy50.musicapp.ui.composables.AddToPlaylistSheet
import com.chaddy50.musicapp.ui.composables.CleanUpWhenNavigatingBackEffect
import com.chaddy50.musicapp.ui.composables.EntityCard
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityHeader
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
import com.chaddy50.musicapp.viewModel.MusicAppViewModel
import kotlinx.coroutines.flow.flowOf

object ArtistsScreen: MusicAppScreen {
    override val route = "artists_screen"

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
            { viewModel.updateSelectedGenre(null) }
        )

        val selectedGenreId = viewModel.selectedGenreId.collectAsStateWithLifecycle()
        val stateHolder = rememberArtistsScreenState(selectedGenreId.value)
        val uiState by stateHolder.uiState.collectAsStateWithLifecycle()
        val allPlaylists by viewModel.allPlaylists.collectAsStateWithLifecycle()

        LaunchedEffect(uiState.screenTitle, uiState.isLoading) {
            if (!uiState.isLoading) {
                onTitleChanged(uiState.screenTitle)
            }
        }

        var artistToAddToPlaylist by remember { mutableStateOf<AlbumArtist?>(null) }
        val playlistsThatArtistIsAlreadyIn by remember(artistToAddToPlaylist?.id) {
            artistToAddToPlaylist?.let { viewModel.getPlaylistsThatAlbumArtistIsAlreadyIn(it.id) } ?: flowOf(emptySet())
        }.collectAsStateWithLifecycle(emptySet())

        EntityScreen(
            uiState.isLoading,
            {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        EntityHeader(viewModel, EntityType.Genre)
                    }

                    items(uiState.artists) { artist ->
                        EntityCard(
                            artist.name,
                            onClick = {
                                viewModel.updateSelectedAlbumArtist(artist.id)
                                navController.navigate(AlbumsScreen.route)
                            },
                            onLongClick = { artistToAddToPlaylist = artist },
                        )
                    }
                }

            },
            onPlay = { viewModel.playCurrentEntity(EntityType.Genre, false) },
            onShuffle = { viewModel.playCurrentEntity(EntityType.Genre, true) },
        )

        artistToAddToPlaylist?.let { artist ->
            AddToPlaylistSheet(
                allPlaylists = allPlaylists,
                playlistsThatEntityIsAlreadyIn = playlistsThatArtistIsAlreadyIn,
                onAddToPlaylist = { playlistId ->
                    viewModel.addEntityToPlaylistById(playlistId, EntityType.AlbumArtist, artist.id)
                },
                onCreateAndAdd = { name ->
                    viewModel.createPlaylistAndAddEntityById(name, EntityType.AlbumArtist, artist.id)
                },
                onDismiss = { artistToAddToPlaylist = null },
            )
        }
    }
}