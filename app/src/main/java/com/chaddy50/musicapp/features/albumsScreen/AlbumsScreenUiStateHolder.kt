package com.chaddy50.musicapp.features.albumsScreen

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@Stable
class AlbumsScreenUiStateHolder(
    selectedAlbumArtistId: Int?,
    selectedSubGenreId: Int?,
    albumRepository: AlbumRepository,
    albumArtistRepository: AlbumArtistRepository,
    genreRepository: GenreRepository,
    coroutineScope: CoroutineScope,
) {
    var uiState: StateFlow<AlbumsScreenUiState>

    init {
        var albums: Flow<List<Album>>
        if (selectedAlbumArtistId != null) {
            if (selectedSubGenreId != null) {
                albums = albumRepository.getAlbumsForArtistInGenre(selectedAlbumArtistId, selectedSubGenreId)
            } else {
                albums = albumRepository.getAlbumsForArtist(selectedAlbumArtistId)
            }
        } else {
            albums = albumRepository.getAllAlbums()
        }

        var artistName: Flow<String?> = flowOf(null)
        if (selectedAlbumArtistId != null) {
            artistName = albumArtistRepository.getAlbumArtistName(selectedAlbumArtistId)
        }

        var subGenreName: Flow<String?> = flowOf(null)
        if (selectedSubGenreId != null) {
            subGenreName = genreRepository.getGenreName(selectedSubGenreId)
        }

        uiState = combine(albums, artistName, subGenreName) { albums, artistName, subGenreName ->
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
    selectedAlbumArtistId: Int?,
    selectedSubGenreId: Int?,
    app: MusicApplication = LocalContext.current.applicationContext as MusicApplication,
    albumRepository: AlbumRepository = app.albumRepository,
    albumArtistRepository: AlbumArtistRepository = app.albumArtistRepository,
    genreRepository: GenreRepository = app.genreRepository,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): AlbumsScreenUiStateHolder {
    return remember(selectedAlbumArtistId, selectedSubGenreId, albumRepository, albumArtistRepository, genreRepository, coroutineScope) {
        AlbumsScreenUiStateHolder(
            selectedAlbumArtistId,
            selectedSubGenreId,
            albumRepository,
            albumArtistRepository,
            genreRepository,
            coroutineScope,
        )
    }
}