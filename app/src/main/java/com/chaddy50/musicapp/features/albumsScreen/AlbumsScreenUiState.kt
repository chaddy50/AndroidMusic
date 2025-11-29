package com.chaddy50.musicapp.features.albumsScreen

import com.chaddy50.musicapp.data.entity.Album

data class AlbumsScreenUiState(
    val screenTitle: String = "Artists",
    val albums: List<Album> = emptyList(),
    val isLoading: Boolean = true
)
