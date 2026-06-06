package com.chaddy50.musicapp.ui.screens.playlistTracksScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.chaddy50.musicapp.data.repository.PlaylistRepository
import com.chaddy50.musicapp.navigation.PlaylistTracksRoute
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityHeaderState
import com.chaddy50.musicapp.utilities.formatMillisecondsIntoMinutesAndSeconds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import com.chaddy50.musicapp.data.entity.Playlist
import com.chaddy50.musicapp.data.entity.Track
import javax.inject.Inject

data class PlaylistTracksScreenState(
    val playlist: Playlist? = null,
    val tracks: List<Track> = emptyList(),
    val isLoading: Boolean = true,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PlaylistTracksScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    playlistRepository: PlaylistRepository,
) : ViewModel() {
    val uiState: StateFlow<PlaylistTracksScreenState>
    val entityHeaderState: StateFlow<EntityHeaderState>

    init {
        val route = savedStateHandle.toRoute<PlaylistTracksRoute>()
        val playlistId = route.playlistId

        val stateFlow = playlistRepository.getPlaylistById(playlistId).flatMapLatest { playlist ->
            if (playlist == null) {
                flowOf(PlaylistTracksScreenState(isLoading = false))
            } else {
                playlistRepository.getTracksForPlaylist(playlistId).flatMapLatest { tracks ->
                    flowOf(PlaylistTracksScreenState(playlist, tracks, false))
                }
            }
        }

        uiState = stateFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlaylistTracksScreenState(isLoading = true),
        )

        entityHeaderState = combine(
            playlistRepository.getPlaylistById(playlistId),
            playlistRepository.getTracksForPlaylist(playlistId),
        ) { playlist, tracks ->
            val playlistDurationMs = tracks.sumOf { it.duration.inWholeMilliseconds }
            EntityHeaderState(
                playlist?.name ?: "Playlist",
                "${tracks.size} tracks - ${formatMillisecondsIntoMinutesAndSeconds(playlistDurationMs)}",
                null,
                null,
                false,
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            EntityHeaderState(),
        )
    }
}
