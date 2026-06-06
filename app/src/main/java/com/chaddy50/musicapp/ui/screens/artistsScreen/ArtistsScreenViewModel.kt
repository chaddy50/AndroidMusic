package com.chaddy50.musicapp.ui.screens.artistsScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.chaddy50.musicapp.data.entity.AlbumArtist
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.navigation.ArtistsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
    albumArtistRepository: AlbumArtistRepository,
    genreRepository: GenreRepository,
) : ViewModel() {
    val uiState: StateFlow<ArtistsScreenUiState>

    init {
        val route = savedStateHandle.toRoute<ArtistsRoute>()
        val genreId = route.genreId

        val artistsToShow: Flow<List<AlbumArtist>> =
            albumArtistRepository.getAlbumArtistsForGenre(genreId)

        val genreName: Flow<String?> = genreRepository.getGenreName(genreId)

        uiState = combine(artistsToShow, genreName) { artists, name ->
            ArtistsScreenUiState(
                screenTitle = name ?: "Artists",
                artists = artists,
                isLoading = false,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ArtistsScreenUiState(isLoading = true),
        )
    }
}
