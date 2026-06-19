package com.chaddy50.froh.ui.screens.playlistsScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaddy50.froh.data.repository.PlaylistRepository
import com.chaddy50.froh.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import com.chaddy50.froh.data.entity.Playlist
import javax.inject.Inject

data class PlaylistsScreenUiState(
    val playlists: List<Playlist> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class PlaylistsScreenViewModel @Inject constructor(
    playlistRepository: PlaylistRepository,
    trackRepository: TrackRepository,
) : ViewModel() {
    val uiState: StateFlow<PlaylistsScreenUiState> = playlistRepository.getAllPlaylists()
        .map { playlists -> PlaylistsScreenUiState(playlists, false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlaylistsScreenUiState(isLoading = true),
        )

    val hasMusic: StateFlow<Boolean> = trackRepository.getNumberOfTracks()
        .map { count -> count > 0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )
}
