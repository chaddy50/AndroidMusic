package com.chaddy50.musicapp.features.screens.subGenresScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.chaddy50.musicapp.MusicApplication
import com.chaddy50.musicapp.data.entity.Genre
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@Stable
class SubGenresScreenStateHolder(
    parentGenreId: Int?,
    albumArtistId: Int?,
    genreRepository: GenreRepository,
    albumArtistRepository: AlbumArtistRepository,
    coroutineScope: CoroutineScope
) {
    var uiState: StateFlow<SubGenresScreenUiState>

    init {
        var genres: Flow<List<Genre>> = flowOf(emptyList())
        if (parentGenreId != null) {
            if (albumArtistId != null) {
                genres = genreRepository.getSubGenresForAlbumArtist(parentGenreId, albumArtistId)
            } else {
                genres = genreRepository.getSubGenres(parentGenreId)
            }
        }

        var albumArtistName: Flow<String?> = flowOf(null)
        if (albumArtistId != null) {
            albumArtistName = albumArtistRepository.getAlbumArtistName(albumArtistId)
        }

        uiState = combine(genres, albumArtistName) { genres, albumArtistName ->
            SubGenresScreenUiState(
                albumArtistName ?: "Sub-genres",
                genres,
                false
            )
        }.stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5_000),
            SubGenresScreenUiState(isLoading = true)
        )
    }
}

@Composable
fun rememberSubGenresScreenState(
    parentGenreId: Int?,
    albumArtistId: Int?,
    app: MusicApplication = LocalContext.current.applicationContext as MusicApplication,
    genreRepository: GenreRepository = app.genreRepository,
    albumArtistRepository: AlbumArtistRepository = app.albumArtistRepository,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) : SubGenresScreenStateHolder {
    return remember(parentGenreId, albumArtistId, genreRepository, albumArtistRepository, coroutineScope) {
        SubGenresScreenStateHolder(
            parentGenreId,
            albumArtistId,
            genreRepository,
            albumArtistRepository,
            coroutineScope,
        )
    }
}