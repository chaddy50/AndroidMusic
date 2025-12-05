package com.chaddy50.musicapp.features.artistsScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.chaddy50.musicapp.MusicApplication
import com.chaddy50.musicapp.data.entity.AlbumArtist
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
class ArtistsScreenStateHolder(
    genreId: Int?,
    albumArtistRepository: AlbumArtistRepository,
    genreRepository: GenreRepository,
    coroutineScope: CoroutineScope
) {
    var uiState: StateFlow<ArtistsScreenUiState>

    init {
        var artistsToShow: Flow<List<AlbumArtist>>
        if (genreId != null) {
            artistsToShow = albumArtistRepository.getAlbumArtistsForGenre(genreId)
        } else {
            artistsToShow = albumArtistRepository.getAllAlbumArtists()
        }

        var genreName: Flow<String?> = flowOf(null)
        if (genreId != null) {
            genreName = genreRepository.getGenreName(genreId)
        }

        uiState = combine(artistsToShow, genreName) { artistsToShow, genreName ->
            ArtistsScreenUiState(
                screenTitle = genreName ?: "Artists",
                artists = artistsToShow,
                isLoading = false
            )
        }.stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ArtistsScreenUiState(isLoading = true)
        )
    }
}

@Composable
fun rememberArtistsScreenState(
    genreId: Int?,
    app: MusicApplication = LocalContext.current.applicationContext as MusicApplication,
    albumArtistRepository: AlbumArtistRepository = app.albumArtistRepository,
    genreRepository: GenreRepository = app.genreRepository,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): ArtistsScreenStateHolder {
    return remember(genreId, albumArtistRepository, genreRepository, coroutineScope) {
        ArtistsScreenStateHolder(
            genreId,
            albumArtistRepository,
            genreRepository,
            coroutineScope,
        )
    }
}