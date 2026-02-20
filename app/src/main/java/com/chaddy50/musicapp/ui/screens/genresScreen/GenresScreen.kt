package com.chaddy50.musicapp.ui.screens.genresScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.chaddy50.musicapp.data.entity.Genre
import com.chaddy50.musicapp.ui.composables.AddToPlaylistSheet
import com.chaddy50.musicapp.ui.composables.EntityCard
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
import com.chaddy50.musicapp.ui.screens.MusicAppScreen
import com.chaddy50.musicapp.ui.screens.artistsScreen.ArtistsScreen
import com.chaddy50.musicapp.viewModel.MusicAppViewModel
import kotlinx.coroutines.flow.flowOf

object GenresScreen : MusicAppScreen {
    override val route = "genres_screen"

    @Composable
    override fun Content(
        viewModel: MusicAppViewModel,
        navController: NavController,
        backStackEntry: NavBackStackEntry,
        onTitleChanged: (title: String) -> Unit,
    ) {
        val stateHolder = rememberGenresScreenState()
        val uiState by stateHolder.uiState.collectAsStateWithLifecycle()
        val allPlaylists by viewModel.allPlaylists.collectAsStateWithLifecycle()

        var genreToAddToPlaylist by remember { mutableStateOf<Genre?>(null) }
        val playlistsThatGenreIsAlreadyIn by remember(genreToAddToPlaylist?.id) {
            genreToAddToPlaylist?.let { viewModel.getPlaylistsThatGenreIsAlreadyIn(it.id) } ?: flowOf(emptySet())
        }.collectAsStateWithLifecycle(emptySet())

        EntityScreen(
            uiState.isLoading,
            {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.genres) { genre ->
                        EntityCard(
                            genre.name,
                            onClick = {
                                viewModel.updateSelectedGenre(genre.id)
                                navController.navigate(ArtistsScreen.route)
                            },
                            onLongClick = { genreToAddToPlaylist = genre },
                        )
                    }
                }

            },
            onPlay = { viewModel.playCurrentEntity(EntityType.All, false) },
            onShuffle = { viewModel.playCurrentEntity(EntityType.All, true) },
        )

        genreToAddToPlaylist?.let { genre ->
            AddToPlaylistSheet(
                allPlaylists = allPlaylists,
                playlistsThatEntityIsAlreadyIn = playlistsThatGenreIsAlreadyIn,
                onAddToPlaylist = { playlistId ->
                    viewModel.addEntityToPlaylistById(playlistId, EntityType.Genre, genre.id)
                },
                onCreateAndAdd = { name ->
                    viewModel.createPlaylistAndAddEntityById(name, EntityType.Genre, genre.id)
                },
                onDismiss = { genreToAddToPlaylist = null },
            )
        }
    }
}