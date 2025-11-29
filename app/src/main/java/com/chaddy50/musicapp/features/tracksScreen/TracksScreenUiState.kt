package com.chaddy50.musicapp.features.tracksScreen

import com.chaddy50.musicapp.data.entity.Track

data class TracksScreenUiState(
    val screenTitle: String = "Tracks",
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = true,
)
