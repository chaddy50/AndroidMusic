package com.chaddy50.musicapp.ui.screens.playlistsScreen

import com.chaddy50.musicapp.data.entity.Playlist

data class PlaylistsScreenUiState(
    val playlists: List<Playlist> = emptyList(),
    val isLoading: Boolean = true,
)