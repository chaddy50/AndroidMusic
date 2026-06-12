package com.chaddy50.musicapp.ui.screens.artistsScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.chaddy50.musicapp.data.ClassicalGenreConfig
import com.chaddy50.musicapp.data.entity.AlbumArtist
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.repository.PlaylistRepository
import com.chaddy50.musicapp.navigation.ArtistsRoute
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityHeaderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ArtistsScreenUiState(
    val screenTitle: String = "Artists",
    val artists: List<AlbumArtist> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class ArtistsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    classicalGenreConfig: ClassicalGenreConfig,
    albumArtistRepository: AlbumArtistRepository,
    genreRepository: GenreRepository,
    playlistRepository: PlaylistRepository,
) : ViewModel() {
    val uiState: StateFlow<ArtistsScreenUiState>
    val entityHeaderState: StateFlow<EntityHeaderState>

    init {
        val route = savedStateHandle.toRoute<ArtistsRoute>()
        val genreId = route.genreId
        val classicalGenreId = classicalGenreConfig.classicalGenreId

        val screenTitle = route.title

        uiState = albumArtistRepository.getAlbumArtistsForGenre(genreId)
            .map { artists ->
                ArtistsScreenUiState(
                    screenTitle = screenTitle,
                    artists = artists,
                    isLoading = false,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ArtistsScreenUiState(isLoading = true),
            )

        entityHeaderState = combine(
            genreRepository.getGenreById(genreId),
            albumArtistRepository.getNumberOfAlbumArtistsForGenre(genreId),
            playlistRepository.getPlaylistIdsContainingGenre(genreId),
        ) { genre, numberOfAlbumArtists, playlistsThatGenreIsAlreadyIn ->
            val artistLabel = if (genre?.id == classicalGenreId) "composers" else "artists"
            EntityHeaderState(
                genre?.name ?: "Genre",
                "$numberOfAlbumArtists $artistLabel",
                null,
                null,
                false,
                playlistsThatGenreIsAlreadyIn,
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            EntityHeaderState(),
        )

    }
}
