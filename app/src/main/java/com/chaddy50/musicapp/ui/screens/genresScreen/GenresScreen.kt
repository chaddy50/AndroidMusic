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
import androidx.navigation.NavController
import com.chaddy50.musicapp.data.entity.Genre
import com.chaddy50.musicapp.navigation.ArtistsRoute
import com.chaddy50.musicapp.ui.composables.AddToPlaylistSheet
import com.chaddy50.musicapp.ui.composables.EntityCard
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.viewModel.MusicAppViewModel
import kotlinx.coroutines.flow.flowOf

@Composable
fun GenresScreen(
    viewModel: MusicAppViewModel,
    navController: NavController,
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
                            navController.navigate(ArtistsRoute(genreId = genre.id))
                        },
                        onLongClick = { genreToAddToPlaylist = genre },
                    )
                }
            }

        },
        onPlay = { viewModel.playAllTracks(false) },
        onShuffle = { viewModel.playAllTracks(true) },
    )

    genreToAddToPlaylist?.let { genre ->
        AddToPlaylistSheet(
            allPlaylists = allPlaylists,
            playlistsThatEntityIsAlreadyIn = playlistsThatGenreIsAlreadyIn,
            onAddToPlaylist = { playlistId ->
                viewModel.addGenreToPlaylist(playlistId, genre.id)
            },
            onCreateAndAdd = { name ->
                viewModel.createPlaylistAndAddGenre(name, genre.id)
            },
            onDismiss = { genreToAddToPlaylist = null },
        )
    }
}
