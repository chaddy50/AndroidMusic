package com.chaddy50.musicapp.ui.screens.tracksScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.data.entity.AlbumArtist
import com.chaddy50.musicapp.data.entity.Track
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.AlbumRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import com.chaddy50.musicapp.navigation.TracksRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class TracksScreenUiState(
    val screenTitle: String = "Tracks",
    val tracks: List<Track> = emptyList(),
    val album: Album? = null,
    val albumArtist: AlbumArtist? = null,
    val isLoading: Boolean = true,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TracksScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    trackRepository: TrackRepository,
    albumRepository: AlbumRepository,
    albumArtistRepository: AlbumArtistRepository,
) : ViewModel() {
    val uiState: StateFlow<TracksScreenUiState>

    init {
        val route = savedStateHandle.toRoute<TracksRoute>()
        val albumId = route.albumId
        val performanceId = if (route.performanceId == -1L) null else route.performanceId

        val tracks: Flow<List<Track>> = if (performanceId != null) {
            trackRepository.getTracksForPerformance(performanceId)
        } else {
            trackRepository.getTracksForAlbum(albumId)
        }

        val album: Flow<Album?> = albumRepository.getAlbumById(albumId)

        val albumArtist: Flow<AlbumArtist?> = album.flatMapLatest { album ->
            if (album != null) {
                albumArtistRepository.getAlbumArtistById(album.artistId)
            } else {
                flowOf(null)
            }
        }

        uiState = combine(tracks, album, albumArtist) { tracks, album, albumArtist ->
            TracksScreenUiState(
                album?.title ?: "Tracks",
                tracks,
                album,
                albumArtist,
                false,
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            TracksScreenUiState(isLoading = true),
        )
    }
}
