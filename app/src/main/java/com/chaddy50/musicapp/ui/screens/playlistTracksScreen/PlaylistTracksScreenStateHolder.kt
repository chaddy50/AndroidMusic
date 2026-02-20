package com.chaddy50.musicapp.ui.screens.playlistTracksScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.chaddy50.musicapp.MusicApplication
import com.chaddy50.musicapp.data.repository.PlaylistRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
@Stable
class PlaylistTracksScreenUiStateHolder(
    playlistId: Long?,
    playlistRepository: PlaylistRepository,
    coroutineScope: CoroutineScope,
) {
    val uiState: StateFlow<PlaylistTracksScreenState>

    init {
        val stateFlow = if (playlistId == null) {
            flowOf(PlaylistTracksScreenState(isLoading = false))
        } else {
            playlistRepository.getPlaylistById(playlistId).flatMapLatest { playlist ->
                if (playlist == null) {
                    flowOf(PlaylistTracksScreenState(isLoading = false))
                } else {
                    playlistRepository.getTracksForPlaylist(playlistId).flatMapLatest { tracks ->
                        flowOf(PlaylistTracksScreenState(playlist, tracks, false))
                    }
                }
            }
        }

        uiState = stateFlow.stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlaylistTracksScreenState(isLoading = true),
        )
    }
}

@Composable
fun rememberPlaylistTracksScreenState(
    playlistId: Long?,
    app: MusicApplication = LocalContext.current.applicationContext as MusicApplication,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): PlaylistTracksScreenUiStateHolder {
    return remember(playlistId, coroutineScope) {
        PlaylistTracksScreenUiStateHolder(playlistId, app.playlistRepository, coroutineScope)
    }
}