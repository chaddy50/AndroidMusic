package com.chaddy50.musicapp.ui.screens.playlistsScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.chaddy50.musicapp.MusicApplication
import com.chaddy50.musicapp.data.repository.PlaylistRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Stable
class PlaylistsScreenUiStateHolder(
    playlistRepository: PlaylistRepository,
    coroutineScope: CoroutineScope,
) {
    val uiState: StateFlow<PlaylistsScreenUiState> = playlistRepository.getAllPlaylists()
        .map { playlists -> PlaylistsScreenUiState(playlists, false) }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PlaylistsScreenUiState(isLoading = true),
        )
}

@Composable
fun rememberPlaylistsScreenState(
    app: MusicApplication = LocalContext.current.applicationContext as MusicApplication,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): PlaylistsScreenUiStateHolder {
    return remember(coroutineScope) {
        PlaylistsScreenUiStateHolder(app.playlistRepository, coroutineScope)
    }
}