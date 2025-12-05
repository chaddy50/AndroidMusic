package com.chaddy50.musicapp.features.subGenresScreen

import com.chaddy50.musicapp.data.entity.Genre

data class SubGenresScreenUiState(
    val screenTitle: String = "Sub-genres",
    val genres: List<Genre> = emptyList(),
    val isLoading: Boolean = true
)
