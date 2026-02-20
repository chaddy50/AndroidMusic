package com.chaddy50.musicapp.ui.screens.playlistTracksScreen

import com.chaddy50.musicapp.data.entity.Playlist
import com.chaddy50.musicapp.data.entity.Track

data class PlaylistTracksScreenState(
    val playlist: Playlist? = null,
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = true,
)