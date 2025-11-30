package com.chaddy50.musicapp.features.tracksScreen

import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.data.entity.AlbumArtist
import com.chaddy50.musicapp.data.entity.Track

data class TracksScreenUiState(
    val screenTitle: String = "Tracks",
    val tracks: List<Track> = emptyList(),
    val album: Album? = null,
    val albumArtist: AlbumArtist? = null,
    val isLoading: Boolean = true,
)
