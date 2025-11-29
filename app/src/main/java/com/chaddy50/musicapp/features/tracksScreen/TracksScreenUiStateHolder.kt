package com.chaddy50.musicapp.features.tracksScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.chaddy50.musicapp.MusicApplication
import com.chaddy50.musicapp.data.entity.Track
import com.chaddy50.musicapp.data.repository.AlbumRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@Stable
class TracksScreenUiStateHolder(
    albumId: Int,
    trackRepository: TrackRepository,
    albumRepository: AlbumRepository,
    coroutineScope: CoroutineScope,
) {
    lateinit var uiState: StateFlow<TracksScreenUiState>

    init {
        var tracks: Flow<List<Track>> = flowOf(emptyList())
        if (albumId != 0) {
            tracks = trackRepository.getTracksForAlbum(albumId)
        }

        var albumName: Flow<String?> = flowOf(null)
        if (albumId != 0) {
            albumName = albumRepository.getAlbumName(albumId)
        }

        uiState = combine(tracks, albumName) { tracks, albumName ->
            TracksScreenUiState(
                albumName ?: "Tracks",
                tracks,
                false,
            )
        }.stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5_000),
            TracksScreenUiState(isLoading = true)
        )
    }
}

@Composable
fun rememberTracksScreenState(
    albumId: Int,
    app: MusicApplication = LocalContext.current.applicationContext as MusicApplication,
    trackRepository: TrackRepository = app.trackRepository,
    albumRepository: AlbumRepository = app.albumRepository,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): TracksScreenUiStateHolder {
    return remember(albumId, trackRepository, albumRepository, coroutineScope) {
        TracksScreenUiStateHolder(
            albumId,
            trackRepository,
            albumRepository,
            coroutineScope
        )
    }
}