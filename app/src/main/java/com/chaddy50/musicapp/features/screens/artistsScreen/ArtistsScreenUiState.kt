package com.chaddy50.musicapp.features.screens.artistsScreen

import com.chaddy50.musicapp.data.entity.AlbumArtist

data class ArtistsScreenUiState (
    val screenTitle: String = "Artists",
    val artists: List<AlbumArtist> = emptyList(),
    val isLoading: Boolean = true,
)