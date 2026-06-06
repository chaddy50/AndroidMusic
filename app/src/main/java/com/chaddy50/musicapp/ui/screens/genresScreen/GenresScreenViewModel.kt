package com.chaddy50.musicapp.ui.screens.genresScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaddy50.musicapp.data.entity.Genre
import com.chaddy50.musicapp.data.repository.GenreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class GenresScreenUiState(
    val screenTitle: String = "Genres",
    val genres: List<Genre> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class GenresScreenViewModel @Inject constructor(
    genreRepository: GenreRepository,
) : ViewModel() {
    val uiState: StateFlow<GenresScreenUiState> = genreRepository.getAllTopLevelGenres()
        .map { genres ->
            GenresScreenUiState("Genres", genres, false)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GenresScreenUiState(isLoading = true)
        )
}
