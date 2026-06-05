package com.chaddy50.musicapp.viewModel

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import com.chaddy50.musicapp.data.scanner.MusicScanner
import com.chaddy50.musicapp.data.entity.Playlist
import com.chaddy50.musicapp.data.entity.Track
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.repository.PlaylistRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import com.chaddy50.musicapp.ui.composables.nowPlayingBar.NowPlayingState
import com.chaddy50.musicapp.data.entity.Genre
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MusicAppViewModel(
    application: Application,
    private val musicScanner: MusicScanner,
    private val trackRepository: TrackRepository,
    private val genreRepository: GenreRepository,
    private val playlistRepository: PlaylistRepository,
) : ViewModel() {
    val nowPlayingState = NowPlayingState(application, viewModelScope)
    private val controller: MediaController? get() = nowPlayingState.controller

    // In-screen filter for AlbumsScreen sub-genre filter button (not navigation state)
    private val _selectedSubGenreId = MutableStateFlow<Long?>(null)
    val selectedSubGenreId = _selectedSubGenreId.asStateFlow()

    private val _isScanInProgress = MutableStateFlow<Boolean>(false)
    val isScanInProgress = _isScanInProgress.asStateFlow()

    private val _scanProgress = MutableStateFlow(0f)
    val scanProgress = _scanProgress.asStateFlow()

    val allPlaylists: StateFlow<List<Playlist>> = playlistRepository.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    var classicalGenreId: Long? = null

    init {
        viewModelScope.launch {
            initializeClassicalGenreId()

            musicScanner.scanProgress.collect {
                _scanProgress.value = it
            }
        }
    }

    fun updateSelectedSubGenre(genreId: Long?) {
        _selectedSubGenreId.value = genreId
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getSubGenresForAlbumArtist(genreId: Long, albumArtistId: Long): Flow<List<Genre>> {
        return if (genreId == classicalGenreId) {
            genreRepository.getSubGenresForAlbumArtist(genreId, albumArtistId)
        } else {
            flowOf(emptyList())
        }
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

    // --- Playback methods (explicit IDs instead of reading from selection state) ---

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

    // --- Playlist query methods ---

    fun getPlaylistsThatTrackIsAlreadyIn(trackId: Long): Flow<Set<Long>> =
        playlistRepository.getPlaylistIdsContainingTrack(trackId)

    fun getPlaylistsThatAlbumIsAlreadyIn(albumId: Long): Flow<Set<Long>> =
        playlistRepository.getPlaylistIdsContainingAlbum(albumId)

    fun getPlaylistsThatAlbumArtistIsAlreadyIn(albumArtistId: Long): Flow<Set<Long>> =
        playlistRepository.getPlaylistIdsContainingAlbumArtist(albumArtistId)

    fun getPlaylistsThatGenreIsAlreadyIn(genreId: Long): Flow<Set<Long>> =
        playlistRepository.getPlaylistIdsContainingGenre(genreId)

    // --- Playlist mutation methods (explicit IDs) ---

    fun addGenreToPlaylist(playlistId: Long, genreId: Long) {
        viewModelScope.launch {
            val tracks = trackRepository.getTracksForGenre(genreId).first()
            tracks.forEach { track -> playlistRepository.addTrackToPlaylist(playlistId, track.id) }
        }
    }

    fun addAlbumArtistToPlaylist(playlistId: Long, albumArtistId: Long) {
        viewModelScope.launch {
            val tracks = trackRepository.getTracksForAlbumArtist(albumArtistId).first()
            tracks.forEach { track -> playlistRepository.addTrackToPlaylist(playlistId, track.id) }
        }
    }

    fun addAlbumToPlaylist(playlistId: Long, albumId: Long) {
        viewModelScope.launch {
            val tracks = trackRepository.getTracksForAlbum(albumId).first()
            tracks.forEach { track -> playlistRepository.addTrackToPlaylist(playlistId, track.id) }
        }
    }

    fun addPlaylistTracksToPlaylist(targetPlaylistId: Long, sourcePlaylistId: Long) {
        viewModelScope.launch {
            val tracks = playlistRepository.getTracksForPlaylist(sourcePlaylistId).first()
            tracks.forEach { track -> playlistRepository.addTrackToPlaylist(targetPlaylistId, track.id) }
        }
    }

    fun createPlaylistAndAddGenre(name: String, genreId: Long) {
        viewModelScope.launch {
            val playlistId = playlistRepository.createPlaylist(name)
            val tracks = trackRepository.getTracksForGenre(genreId).first()
            tracks.forEach { track -> playlistRepository.addTrackToPlaylist(playlistId, track.id) }
        }
    }

    fun createPlaylistAndAddAlbumArtist(name: String, albumArtistId: Long) {
        viewModelScope.launch {
            val playlistId = playlistRepository.createPlaylist(name)
            val tracks = trackRepository.getTracksForAlbumArtist(albumArtistId).first()
            tracks.forEach { track -> playlistRepository.addTrackToPlaylist(playlistId, track.id) }
        }
    }

    fun createPlaylistAndAddAlbum(name: String, albumId: Long) {
        viewModelScope.launch {
            val playlistId = playlistRepository.createPlaylist(name)
            val tracks = trackRepository.getTracksForAlbum(albumId).first()
            tracks.forEach { track -> playlistRepository.addTrackToPlaylist(playlistId, track.id) }
        }
    }

    fun createPlaylist(name: String) {
        viewModelScope.launch { playlistRepository.createPlaylist(name) }
    }

    fun createPlaylistAndAddTrack(name: String, track: Track) {
        viewModelScope.launch {
            val playlistId = playlistRepository.createPlaylist(name)
            playlistRepository.addTrackToPlaylist(playlistId, track.id)
        }
    }

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch { playlistRepository.deletePlaylist(playlist) }
    }

    fun addTrackToPlaylist(playlistId: Long, track: Track) {
        viewModelScope.launch { playlistRepository.addTrackToPlaylist(playlistId, track.id) }
    }

    fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        viewModelScope.launch { playlistRepository.removeTrackFromPlaylist(playlistId, trackId) }
    }

    override fun onCleared() {
        super.onCleared()
        nowPlayingState.release()
    }
}
