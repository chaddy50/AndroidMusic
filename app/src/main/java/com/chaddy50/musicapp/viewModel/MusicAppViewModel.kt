package com.chaddy50.musicapp.viewModel

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import com.chaddy50.musicapp.data.MusicScanner
import com.chaddy50.musicapp.data.entity.Track
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import com.chaddy50.musicapp.features.nowPlayingBar.NowPlayingState
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MusicAppViewModel(
    application: Application,
    private val musicScanner: MusicScanner,
    private val trackRepository: TrackRepository,
    private val genreRepository: GenreRepository,
) : ViewModel() {
    val nowPlayingState = NowPlayingState(application, viewModelScope)
    private val controller: MediaController? get() = nowPlayingState.controller

    private val _selectedGenreId = MutableStateFlow<Int?>(null)
    val selectedGenreId = _selectedGenreId.asStateFlow()

    private val _selectedSubGenreId = MutableStateFlow<Int?>(null)
    val selectedSubGenreId = _selectedSubGenreId.asStateFlow()

    private val _selectedAlbumArtistId = MutableStateFlow<Int?>(null)
    val selectedAlbumArtistId = _selectedAlbumArtistId.asStateFlow()

    private val _selectedAlbumId = MutableStateFlow<Int?>(null)
    val selectedAlbumId = _selectedAlbumId.asStateFlow()

    private val _selectedPerformanceId = MutableStateFlow<Int?>(null)
    val selectedPerformanceId = _selectedPerformanceId.asStateFlow()

    private val _isScanInProgress = MutableStateFlow<Boolean>(false)
    val isScanInProgress = _isScanInProgress.asStateFlow()

    private val _scanProgress = MutableStateFlow(0f)
    val scanProgress = _scanProgress.asStateFlow()

    var classicalGenreId: Int? = null

    init {
        viewModelScope.launch {
            initializeClassicalGenreId()

            musicScanner.scanProgress.collect {
                _scanProgress.value = it
            }
        }
    }


    fun updateSelectedGenre(genreId: Int?) {
        _selectedGenreId.value = genreId
    }

    fun updateSelectedAlbumArtist(albumArtistId: Int?) {
        _selectedAlbumArtistId.value = albumArtistId
    }

    fun updateSelectedSubGenre(genreId: Int?) {
        _selectedSubGenreId.value = genreId
    }

    fun updateSelectedAlbum(albumId: Int?) {
        _selectedAlbumId.value = albumId
    }

    fun updateSelectedPerformance(performanceId: Int?) {
        _selectedPerformanceId.value = performanceId
    }

    suspend fun getTrackCount(): Int {
        return trackRepository.getNumberOfTracksSuspend()
    }

    suspend fun refreshLibrary() {
        _isScanInProgress.value = true
        musicScanner.scan()
        initializeClassicalGenreId()
        _isScanInProgress.value = false
    }

    private suspend fun initializeClassicalGenreId() {
        classicalGenreId = genreRepository.getGenreByName("Classical")?.id
    }

    fun playCurrentEntity(type: EntityType, shouldPlayShuffled: Boolean) {
        viewModelScope.launch {
            val tracksToPlay: List<Track> = when(type) {
                EntityType.All -> {
                    trackRepository.getAllTracks().first()
                }
                EntityType.Genre -> {
                    val genreId = _selectedGenreId.value
                    if (genreId != null) trackRepository.getTracksForGenre(genreId).first() else emptyList()
                }
                EntityType.AlbumArtist -> {
                    val albumArtistId = _selectedAlbumArtistId.value
                    if (albumArtistId != null) trackRepository.getTracksForAlbumArtist(albumArtistId).first() else emptyList()
                }
                EntityType.Album -> {
                    val albumId = _selectedAlbumId.value
                    val performanceId = _selectedPerformanceId.value
                    if (albumId != null) {
                        if (performanceId != null) {
                            trackRepository.getTracksForPerformance(performanceId).first()
                        } else {
                            trackRepository.getTracksForAlbum(albumId).first()
                        }
                    } else emptyList()
                }
                else -> emptyList()
            }

            if (tracksToPlay.isNotEmpty()) {
                val mediaItems = tracksToPlay.map { track ->
                    buildMediaItem(track)
                }

                controller?.let { controller ->
                    controller.shuffleModeEnabled = shouldPlayShuffled
                    controller.setMediaItems(mediaItems)
                    controller.prepare()
                    controller.play()
                }
            }
        }
    }

    fun playTrack(track: Track, allTracks: List<Track>) {
        val mediaItems = allTracks.map { buildMediaItem(it) }
        val trackIndex = allTracks.indexOfFirst { it.id == track.id }

        controller?.let { controller ->
            controller.setMediaItems(mediaItems, trackIndex, 0)
            controller.prepare()
            controller.play()
        }
    }

    private fun buildMediaItem(track: Track): MediaItem {
        val metadata = MediaMetadata.Builder()
            .setTitle(track.title)
            .setArtist(if (track.parentGenreId == classicalGenreId) "${track.albumArtistName} • ${track.albumName}" else track.artistName)
            .setGenre(track.genreName)
            .setAlbumArtist(track.albumArtistName)
            .setAlbumTitle(track.albumName)
            .setDurationMs(track.duration.inWholeMilliseconds)
            .setArtworkUri(track.artworkPath?.toUri())
            .build()

        return MediaItem.Builder()
            .setMediaId(track.id.toString())
            .setUri(track.uri)
            .setMediaMetadata(metadata)
            .build()
    }

    override fun onCleared() {
        super.onCleared()
        nowPlayingState.release()
    }
}