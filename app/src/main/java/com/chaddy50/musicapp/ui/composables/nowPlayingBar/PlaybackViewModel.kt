package com.chaddy50.musicapp.ui.composables.nowPlayingBar

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import com.chaddy50.musicapp.data.ClassicalGenreConfig
import com.chaddy50.musicapp.data.entity.Track
import com.chaddy50.musicapp.data.repository.PlaylistRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val application: Application,
    private val classicalGenreConfig: ClassicalGenreConfig,
    private val trackRepository: TrackRepository,
    private val playlistRepository: PlaylistRepository,
) : ViewModel() {
    val nowPlayingState = NowPlayingState(application, viewModelScope)
    private val controller: MediaController? get() = nowPlayingState.controller

    private val classicalGenreId: Long? get() = classicalGenreConfig.classicalGenreId

    fun playAllTracks(shuffled: Boolean) {
        viewModelScope.launch {
            val tracks = trackRepository.getAllTracks().first()
            playTracks(tracks, shuffled)
        }
    }

    fun playTracksForGenre(genreId: Long, shuffled: Boolean) {
        viewModelScope.launch {
            val tracks = trackRepository.getTracksForGenre(genreId).first()
            playTracks(tracks, shuffled)
        }
    }

    fun playTracksForAlbumArtist(albumArtistId: Long, subGenreId: Long?, shuffled: Boolean) {
        viewModelScope.launch {
            val tracks = if (subGenreId != null) {
                trackRepository.getTracksForAlbumArtistInGenre(albumArtistId, subGenreId).first()
            } else {
                trackRepository.getTracksForAlbumArtist(albumArtistId).first()
            }
            playTracks(tracks, shuffled)
        }
    }

    fun playTracksForAlbum(albumId: Long, performanceId: Long?, shuffled: Boolean) {
        viewModelScope.launch {
            val tracks = if (performanceId != null) {
                trackRepository.getTracksForPerformance(performanceId).first()
            } else {
                trackRepository.getTracksForAlbum(albumId).first()
            }
            playTracks(tracks, shuffled)
        }
    }

    fun playTracksForPlaylist(playlistId: Long, shuffled: Boolean) {
        viewModelScope.launch {
            val tracks = playlistRepository.getTracksForPlaylist(playlistId).first()
            playTracks(tracks, shuffled)
        }
    }

    private fun playTracks(tracks: List<Track>, shuffled: Boolean) {
        if (tracks.isNotEmpty()) {
            val mediaItems = tracks.map { buildMediaItem(it) }
            controller?.let { controller ->
                controller.shuffleModeEnabled = shuffled
                controller.setMediaItems(mediaItems)
                controller.prepare()
                controller.play()
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
        var artist = track.artistName
        if (track.parentGenreId == classicalGenreId) {
            artist += " - ${track.year}"
        }

        val metadata = MediaMetadata.Builder()
            .setTitle(track.title)
            .setArtist(artist)
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
