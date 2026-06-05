package com.chaddy50.musicapp.ui.screens.albumsScreen

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
    genreId: Long,
    albumArtistId: Long,
    classicalGenreId: Long?,
    viewModel: MusicAppViewModel,
    albumRepository: AlbumRepository,
    albumArtistRepository: AlbumArtistRepository,
    genreRepository: GenreRepository,
    coroutineScope: CoroutineScope,
) {
    var uiState: StateFlow<AlbumsScreenUiState>

    init {
        val isClassical = genreId == classicalGenreId

        val albums: StateFlow<List<Album>> = viewModel.selectedSubGenreId
            .flatMapLatest { selectedSubGenreId ->
                if (selectedSubGenreId != null) {
                    albumRepository.getAlbumsForArtistInGenre(albumArtistId, selectedSubGenreId, isClassical)
                } else {
                    albumRepository.getAlbumsForArtist(albumArtistId, isClassical)
                }
            }.stateIn(coroutineScope, SharingStarted.WhileSubscribed(5_000), emptyList())

        val artistName = albumArtistRepository.getAlbumArtistName(albumArtistId)

        val subGenreName = viewModel.selectedSubGenreId.flatMapLatest { selectedSubGenreId ->
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
    genreId: Long,
    albumArtistId: Long,
    viewModel: MusicAppViewModel,
    app: MusicApplication = LocalContext.current.applicationContext as MusicApplication,
    albumRepository: AlbumRepository = app.albumRepository,
    albumArtistRepository: AlbumArtistRepository = app.albumArtistRepository,
    genreRepository: GenreRepository = app.genreRepository,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): AlbumsScreenUiStateHolder {
    return remember(genreId, albumArtistId, viewModel, albumRepository, albumArtistRepository, genreRepository, coroutineScope) {
        AlbumsScreenUiStateHolder(
            genreId,
            albumArtistId,
            viewModel.classicalGenreId,
            viewModel,
            albumRepository,
            albumArtistRepository,
            genreRepository,
            coroutineScope,
        )
    }
}
