package com.chaddy50.musicapp.features.genresScreen

import com.chaddy50.musicapp.data.entity.Genre

data class SubGenresScreenUiState(
    val screenTitle: String = "Genres",
    val genres: List<Genre> = emptyList(),
    val isLoading: Boolean = true
)
