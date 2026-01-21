package com.chaddy50.musicapp.features.screens.albumsScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.chaddy50.musicapp.MusicApplication
import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.AlbumRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.viewModel.MusicAppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
@Stable
class AlbumsScreenUiStateHolder(
    viewModel: MusicAppViewModel,
    albumRepository: AlbumRepository,
    albumArtistRepository: AlbumArtistRepository,
    genreRepository: GenreRepository,
    coroutineScope: CoroutineScope,
) {
    var uiState: StateFlow<AlbumsScreenUiState>

    init {
        val albums: StateFlow<List<Album>> = combine(
            viewModel.selectedGenreId,
            viewModel.selectedAlbumArtistId,
            viewModel.selectedSubGenreId
        ) { selectedGenreId, selectedAlbumArtistId, selectedSubGenreId ->
            Triple(selectedGenreId, selectedAlbumArtistId, selectedSubGenreId)
        }.flatMapLatest { (selectedGenreId, selectedAlbumArtistId, selectedSubGenreId) ->
            if (selectedAlbumArtistId != null) {
                if (selectedSubGenreId != null) {
                    albumRepository.getAlbumsForArtistInGenre(selectedAlbumArtistId, selectedSubGenreId, selectedGenreId == viewModel.classicalGenreId)
                } else {
                    albumRepository.getAlbumsForArtist(selectedAlbumArtistId, selectedGenreId == viewModel.classicalGenreId)
                }
            } else {
                albumRepository.getAllAlbums()
            }
        }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5_000), emptyList())

        val artistName = viewModel.selectedAlbumArtistId.flatMapLatest { selectedAlbumArtistId ->
            if (selectedAlbumArtistId != null) {
                albumArtistRepository.getAlbumArtistName(selectedAlbumArtistId)
            } else {
                flowOf(null)
            }
        }

        var subGenreName = viewModel.selectedSubGenreId.flatMapLatest { selectedSubGenreId ->
            if (selectedSubGenreId != null) {
                genreRepository.getGenreName(selectedSubGenreId)
            } else {
                flowOf(null)
            }
        }

        uiState = combine(albums, artistName, subGenreName, viewModel.selectedSubGenreId) { albums, artistName, subGenreName, selectedSubGenreId ->
            val screenTitle = if (selectedSubGenreId == null) artistName else "$artistName - $subGenreName"

            AlbumsScreenUiState(
                screenTitle ?: "Albums",
                albums,
                false,
            )
        }.stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5_000),
            AlbumsScreenUiState(isLoading = true)
        )
    }
}

@Composable
fun rememberAlbumsScreenState(
    viewModel: MusicAppViewModel,
    app: MusicApplication = LocalContext.current.applicationContext as MusicApplication,
    albumRepository: AlbumRepository = app.albumRepository,
    albumArtistRepository: AlbumArtistRepository = app.albumArtistRepository,
    genreRepository: GenreRepository = app.genreRepository,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): AlbumsScreenUiStateHolder {
    return remember(viewModel, albumRepository, albumArtistRepository, genreRepository, coroutineScope) {
        AlbumsScreenUiStateHolder(
            viewModel,
            albumRepository,
            albumArtistRepository,
            genreRepository,
            coroutineScope,
        )
    }
}