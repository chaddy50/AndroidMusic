package com.chaddy50.musicapp.ui.screens.settingsScreen.genreMappings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaddy50.musicapp.data.repository.GenreMappingRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GenreSelectionItem(
    val genreId: Long,
    val genreName: String,
    val isSelected: Boolean,
)

data class ClassicalGenreSettingsUiState(
    val genres: List<GenreSelectionItem> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class ClassicalGenreSettingsViewModel @Inject constructor(
    genreRepository: GenreRepository,
    private val genreMappingRepository: GenreMappingRepository,
) : ViewModel() {

    private val selectedGenreNames = MutableStateFlow<Set<String>>(emptySet())
    private var originalSelectedNames: Set<String> = emptySet()

    init {
        viewModelScope.launch {
            val classicalNames = genreMappingRepository.getClassicalGenreNames()
            originalSelectedNames = classicalNames
            selectedGenreNames.value = classicalNames
        }
    }

    val uiState: StateFlow<ClassicalGenreSettingsUiState> = combine(
        genreRepository.getAllGenres(),
        selectedGenreNames,
    ) { genres, selected ->
        ClassicalGenreSettingsUiState(
            genres = genres.map { genre ->
                GenreSelectionItem(
                    genreId = genre.id,
                    genreName = genre.name,
                    isSelected = genre.name in selected,
                )
            },
            isLoading = false,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ClassicalGenreSettingsUiState(),
    )

    fun toggleGenreSelection(genreName: String) {
        val current = selectedGenreNames.value
        selectedGenreNames.value = if (genreName in current) {
            current - genreName
        } else {
            current + genreName
        }
    }

    suspend fun saveSelections(): Boolean {
        val current = selectedGenreNames.value
        val hasChanges = current != originalSelectedNames
        if (hasChanges) {
            genreMappingRepository.saveClassicalGenreNames(current)
            originalSelectedNames = current
        }
        return hasChanges
    }
}
