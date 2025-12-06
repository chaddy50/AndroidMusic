package com.chaddy50.musicapp.features.performancesScreen

import com.chaddy50.musicapp.data.entity.Performance

data class PerformanceScreenUiState(
    val screenTitle: String = "Performance",
    val performances: List<Performance> = emptyList(),
    val isLoading: Boolean = true
)