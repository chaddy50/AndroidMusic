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
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
import com.chaddy50.musicapp.data.entity.Genre
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
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

    private val _selectedGenreId = MutableStateFlow<Long?>(null)
    val selectedGenreId = _selectedGenreId.asStateFlow()

    private val _selectedSubGenreId = MutableStateFlow<Long?>(null)
    val selectedSubGenreId = _selectedSubGenreId.asStateFlow()

    private val _selectedAlbumArtistId = MutableStateFlow<Long?>(null)
    val selectedAlbumArtistId = _selectedAlbumArtistId.asStateFlow()

    private val _selectedAlbumId = MutableStateFlow<Long?>(null)
    val selectedAlbumId = _selectedAlbumId.asStateFlow()

    private val _selectedPerformanceId = MutableStateFlow<Long?>(null)
    val selectedPerformanceId = _selectedPerformanceId.asStateFlow()

    private val _isScanInProgress = MutableStateFlow<Boolean>(false)
    val isScanInProgress = _isScanInProgress.asStateFlow()

    private val _scanProgress = MutableStateFlow(0f)
    val scanProgress = _scanProgress.asStateFlow()

    private val _selectedPlaylistId = MutableStateFlow<Long?>(null)
    val selectedPlaylistId = _selectedPlaylistId.asStateFlow()

    val allPlaylists: StateFlow<List<Playlist>> = playlistRepository.getAllPlaylists()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    var classicalGenreId: Long? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val subGenresForAlbumArtist: StateFlow<List<Genre>> = combine(
        _selectedGenreId,
        _selectedAlbumArtistId
    ) { genreId, albumArtistId ->
        Pair(genreId, albumArtistId)
    }.flatMapLatest { (genreId, albumArtistId) ->
        if (genreId != null && genreId == classicalGenreId && albumArtistId != null) {
            genreRepository.getSubGenresForAlbumArtist(genreId, albumArtistId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init {
        viewModelScope.launch {
            initializeClassicalGenreId()

            musicScanner.scanProgress.collect {
                _scanProgress.value = it
            }
        }
    }


    fun updateSelectedGenre(genreId: Long?) {
        _selectedGenreId.value = genreId
    }

    fun updateSelectedAlbumArtist(albumArtistId: Long?) {
        _selectedAlbumArtistId.value = albumArtistId
    }

    fun updateSelectedSubGenre(genreId: Long?) {
        _selectedSubGenreId.value = genreId
    }

    fun updateSelectedAlbum(albumId: Long?) {
        _selectedAlbumId.value = albumId
    }

    fun updateSelectedPerformance(performanceId: Long?) {
        _selectedPerformanceId.value = performanceId
    }

    fun updateSelectedPlaylist(id: Long?) {
        _selectedPlaylistId.value = id
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
            val tracksToPlay = getTracksForCurrentEntity(type)

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

    fun getPlaylistsThatTrackIsAlreadyIn(trackId: Long): Flow<Set<Long>> =
        playlistRepository.getPlaylistIdsContainingTrack(trackId)

    fun getPlaylistsThatAlbumIsAlreadyIn(albumId: Long): Flow<Set<Long>> =
        playlistRepository.getPlaylistIdsContainingAlbum(albumId)

    fun getPlaylistsThatAlbumArtistIsAlreadyIn(albumArtistId: Long): Flow<Set<Long>> =
        playlistRepository.getPlaylistIdsContainingAlbumArtist(albumArtistId)

    fun getPlaylistsThatGenreIsAlreadyIn(genreId: Long): Flow<Set<Long>> =
        playlistRepository.getPlaylistIdsContainingGenre(genreId)

    fun addEntityToPlaylistById(playlistId: Long, type: EntityType, entityId: Long) {
        viewModelScope.launch {
            val tracks = getTracksForGivenEntity(type, entityId)
            tracks.forEach { track -> playlistRepository.addTrackToPlaylist(playlistId, track.id) }
        }
    }

    fun createPlaylistAndAddEntityById(name: String, type: EntityType, entityId: Long) {
        viewModelScope.launch {
            val playlistId = playlistRepository.createPlaylist(name)
            val tracks = getTracksForGivenEntity(type, entityId)
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

    fun createPlaylistAndAddCurrentEntity(name: String, type: EntityType) {
        viewModelScope.launch {
            val playlistId = playlistRepository.createPlaylist(name)
            val tracksToAdd = getTracksForCurrentEntity(type)
            tracksToAdd.forEach { track ->
                playlistRepository.addTrackToPlaylist(playlistId, track.id)
            }
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

    fun addCurrentEntityToPlaylist(playlistId: Long, type: EntityType) {
        viewModelScope.launch {
            val tracksToAdd = getTracksForCurrentEntity(type)
            tracksToAdd.forEach { track ->
                playlistRepository.addTrackToPlaylist(playlistId, track.id)
            }
        }
    }

    private suspend fun getTracksForCurrentEntity(type: EntityType) : List<Track> {
        return when(type) {
            EntityType.All -> {
                trackRepository.getAllTracks().first()
            }

            EntityType.Genre -> {
                val genreId = _selectedGenreId.value
                if (genreId != null) trackRepository.getTracksForGenre(genreId)
                    .first() else emptyList()
            }

            EntityType.AlbumArtist -> {
                val albumArtistId = _selectedAlbumArtistId.value
                val subGenreId = _selectedSubGenreId.value
                if (albumArtistId != null) {
                    if (subGenreId != null) {
                        trackRepository.getTracksForAlbumArtistInGenre(albumArtistId, subGenreId)
                            .first()
                    } else {
                        trackRepository.getTracksForAlbumArtist(albumArtistId).first()
                    }
                } else emptyList()
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

            EntityType.Playlist -> {
                val playlistId = _selectedPlaylistId.value
                if (playlistId != null) playlistRepository.getTracksForPlaylist(playlistId)
                    .first() else emptyList()
            }

            else -> emptyList()
        }
    }

    private suspend fun getTracksForGivenEntity(type: EntityType, entityId: Long): List<Track> {
        return when (type) {
            EntityType.Album -> trackRepository.getTracksForAlbum(entityId).first()
            EntityType.AlbumArtist -> trackRepository.getTracksForAlbumArtist(entityId).first()
            EntityType.Genre -> trackRepository.getTracksForGenre(entityId).first()
            else -> emptyList()
        }
    }

    override fun onCleared() {
        super.onCleared()
        nowPlayingState.release()
    }
}