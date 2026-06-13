package com.chaddy50.musicapp.ui.screens.tracksScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.chaddy50.musicapp.data.ClassicalGenreConfig
import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.data.entity.AlbumArtist
import com.chaddy50.musicapp.data.entity.Track
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.AlbumRepository
import com.chaddy50.musicapp.data.repository.PerformanceRepository
import com.chaddy50.musicapp.data.repository.PlaylistRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import com.chaddy50.musicapp.navigation.TracksRoute
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityHeaderState
import com.chaddy50.musicapp.utilities.formatMillisecondsIntoMinutesAndSeconds
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
    val isClassical: Boolean = false,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TracksScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    classicalGenreConfig: ClassicalGenreConfig,
    trackRepository: TrackRepository,
    albumRepository: AlbumRepository,
    albumArtistRepository: AlbumArtistRepository,
    performanceRepository: PerformanceRepository,
    playlistRepository: PlaylistRepository,
) : ViewModel() {
    val uiState: StateFlow<TracksScreenUiState>
    val entityHeaderState: StateFlow<EntityHeaderState>

    init {
        val route = savedStateHandle.toRoute<TracksRoute>()
        val albumId = route.albumId
        val genreId = route.genreId
        val performanceId = if (route.performanceId == -1L) null else route.performanceId
        val classicalGenreId = classicalGenreConfig.classicalGenreId
        val isClassical = genreId == classicalGenreId

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
                isClassical,
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            TracksScreenUiState(isLoading = true),
        )

        entityHeaderState = if (performanceId != null) {
            combine(
                albumRepository.getAlbumById(albumId),
                performanceRepository.getPerformanceById(performanceId),
                trackRepository.getTracksForPerformance(performanceId),
            ) { album, performance, performanceTracks ->
                val tracksLabel = if (isClassical) "movements" else "tracks"
                val performanceDurationMs = performanceTracks.sumOf { it.duration.inWholeMilliseconds }
                EntityHeaderState(
                    album?.title ?: "Album",
                    "${performance.year} - ${performance.artistName}",
                    "${performanceTracks.size} $tracksLabel - ${formatMillisecondsIntoMinutesAndSeconds(performanceDurationMs)}",
                    null,
                    false,
                )
            }
        } else {
            albumRepository.getAlbumById(albumId).flatMapLatest { album ->
                if (album == null) {
                    return@flatMapLatest flowOf(EntityHeaderState(isLoading = false))
                }

                combine(
                    albumArtistRepository.getAlbumArtistById(album.artistId),
                    trackRepository.getTracksForAlbum(albumId),
                    performanceRepository.getNumberOfPerformancesForAlbum(albumId),
                    playlistRepository.getPlaylistIdsContainingAlbum(album.id),
                ) { albumArtist, albumTracks, numberOfPerformances, playlistsThatAlbumIsAlreadyIn ->
                    val albumDurationMs = albumTracks.sumOf { it.duration.inWholeMilliseconds }
                    EntityHeaderState(
                        album.title,
                        albumArtist?.name ?: "Unknown Artist",
                        getDetailsForAlbum(album, albumTracks.size, numberOfPerformances, albumDurationMs, isClassical),
                        if (isClassical) null else album.artworkPath,
                        false,
                        playlistsThatAlbumIsAlreadyIn,
                    )
                }
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            EntityHeaderState(),
        )
    }

    private fun getDetailsForAlbum(
        album: Album,
        numberOfTracks: Int,
        numberOfPerformances: Int,
        albumDurationMs: Long,
        isClassical: Boolean,
    ): String {
        var items: List<String> = listOf()
        if (!isClassical) {
            items = items.plus(album.year)
        }
        if (isClassical) {
            items = items.plus("$numberOfPerformances performances")
        } else {
            items = items.plus("$numberOfTracks tracks")
        }
        if (!isClassical) {
            items = items.plus(formatMillisecondsIntoMinutesAndSeconds(albumDurationMs))
        }
        return items.joinToString(" - ")
    }
}
