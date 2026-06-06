package com.chaddy50.musicapp.ui.screens.albumsScreen

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.chaddy50.musicapp.MusicApplication
import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.data.entity.Genre
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.AlbumRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.navigation.AlbumsRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class AlbumsScreenUiState(
    val screenTitle: String = "Artists",
    val albums: List<Album> = emptyList(),
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AlbumsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    application: Application,
    albumRepository: AlbumRepository,
    albumArtistRepository: AlbumArtistRepository,
    genreRepository: GenreRepository,
) : ViewModel() {
    private val _selectedSubGenreId = MutableStateFlow<Long?>(null)
    val selectedSubGenreId = _selectedSubGenreId.asStateFlow()

    val subGenres: StateFlow<List<Genre>>
    val uiState: StateFlow<AlbumsScreenUiState>

    init {
        val route = savedStateHandle.toRoute<AlbumsRoute>()
        val genreId = route.genreId
        val albumArtistId = route.albumArtistId
        val classicalGenreId = (application as MusicApplication).classicalGenreId
        val isClassical = genreId == classicalGenreId

        subGenres = if (genreId == classicalGenreId) {
            genreRepository.getSubGenresForAlbumArtist(genreId, albumArtistId)
        } else {
            flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

        val albums: StateFlow<List<Album>> = _selectedSubGenreId
            .flatMapLatest { selectedSubGenreId ->
                if (selectedSubGenreId != null) {
                    albumRepository.getAlbumsForArtistInGenre(albumArtistId, selectedSubGenreId, isClassical)
                } else {
                    albumRepository.getAlbumsForArtist(albumArtistId, isClassical)
                }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

        val artistName = albumArtistRepository.getAlbumArtistName(albumArtistId)

        val subGenreName = _selectedSubGenreId.flatMapLatest { selectedSubGenreId ->
            if (selectedSubGenreId != null) {
                genreRepository.getGenreName(selectedSubGenreId)
            } else {
                flowOf(null)
            }
        }


        uiState = combine(albums, artistName, subGenreName, _selectedSubGenreId) { albums, artistName, subGenreName, selectedSubGenreId ->
            val screenTitle = if (selectedSubGenreId == null) artistName else "$artistName - $subGenreName"

            AlbumsScreenUiState(
                screenTitle ?: "Albums",
                albums,
                false,
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            AlbumsScreenUiState(isLoading = true),
        )
    }

    fun updateSelectedSubGenreId(subGenreId: Long?) {
        _selectedSubGenreId.value = subGenreId
    }
}
