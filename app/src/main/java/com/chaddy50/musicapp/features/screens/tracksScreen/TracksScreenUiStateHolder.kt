package com.chaddy50.musicapp.features.screens.tracksScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.chaddy50.musicapp.MusicApplication
import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.data.entity.AlbumArtist
import com.chaddy50.musicapp.data.entity.Track
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.AlbumRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
@Stable
class TracksScreenUiStateHolder(
    albumId: Int?,
    performanceId: Int?,
    trackRepository: TrackRepository,
    albumRepository: AlbumRepository,
    albumArtistRepository: AlbumArtistRepository,
    coroutineScope: CoroutineScope,
) {
    var uiState: StateFlow<TracksScreenUiState>

    init {
        var tracks: Flow<List<Track>> = flowOf(emptyList())
        if (performanceId != null) {
            tracks = trackRepository.getTracksForPerformance(performanceId)
        } else if (albumId != null) {
            tracks = trackRepository.getTracksForAlbum(albumId)
        }

        var album: Flow<Album?> = flowOf(null)
        if (albumId != null) {
            album = albumRepository.getAlbumById(albumId)
        }

        val albumArtist: Flow<AlbumArtist?> = album.flatMapLatest { album ->
            if (album != null) {
                albumArtistRepository.getAlbumArtistById(album.artistId)
            } else {
                flowOf(null)
            }
        }

        uiState = combine(
            tracks,
            album,
            albumArtist
        ) { tracks, album, albumArtist ->
            TracksScreenUiState(
                album?.title ?: "Tracks",
                tracks,
                album,
                albumArtist,
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
    albumId: Int?,
    performanceId: Int?,
    app: MusicApplication = LocalContext.current.applicationContext as MusicApplication,
    trackRepository: TrackRepository = app.trackRepository,
    albumRepository: AlbumRepository = app.albumRepository,
    albumArtistRepository: AlbumArtistRepository = app.albumArtistRepository,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): TracksScreenUiStateHolder {
    return remember(albumId, performanceId, trackRepository, albumRepository, albumArtistRepository, coroutineScope) {
        TracksScreenUiStateHolder(
            albumId,
            performanceId,
            trackRepository,
            albumRepository,
            albumArtistRepository,
            coroutineScope
        )
    }
}