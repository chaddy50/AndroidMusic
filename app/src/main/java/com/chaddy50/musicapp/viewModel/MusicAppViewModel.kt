package com.chaddy50.musicapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chaddy50.musicapp.data.MusicScanner
import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.data.entity.AlbumArtist
import com.chaddy50.musicapp.data.entity.Genre
import com.chaddy50.musicapp.data.entity.Track
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.AlbumRepository
import com.chaddy50.musicapp.data.repository.ArtistRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MusicAppViewModel(
    private val musicScanner: MusicScanner,
    private val trackRepository: TrackRepository,
    private val albumRepository: AlbumRepository,
    private val artistRepository: ArtistRepository,
    private val albumArtistRepository: AlbumArtistRepository,
    private val genreRepository: GenreRepository,
) : ViewModel() {
    val genres: StateFlow<List<Genre>> = genreRepository.getAllGenres()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val albumArtists: StateFlow<List<AlbumArtist>> = albumArtistRepository.getAllAlbumArtists()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    suspend fun getTrackCount(): Int {
        return trackRepository.count()
    }

    fun getAlbumArtistsForGenre(genreId: Int): StateFlow<List<AlbumArtist>> {
        return albumArtistRepository.getAlbumArtistsForGenre(genreId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )
    }

    val albums: StateFlow<List<Album>> = albumRepository.getAllAlbums()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun getAlbumsForArtist(artistId: Int): StateFlow<List<Album>> {
        return albumRepository.getAlbumsForArtist(artistId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
    }

    fun getTracksForAlbum(albumId: Int): StateFlow<List<Track>> {
        return trackRepository.getTracksForAlbum(albumId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList(),
            )
    }

    fun getGenreName(genreId: Int): StateFlow<String?> {
        return genreRepository.getGenreName(genreId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = "",
            )
    }

    fun getAlbumArtistName(albumArtistId: Int): StateFlow<String?> {
        return albumArtistRepository.getAlbumArtistName(albumArtistId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = "",
            )
    }

    fun getAlbumName(albumId: Int): StateFlow<String?> {
        return albumRepository.getAlbumName(albumId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = "",
            )
    }

    fun refreshLibrary() {
        viewModelScope.launch {
            musicScanner.scan()
        }
    }
}