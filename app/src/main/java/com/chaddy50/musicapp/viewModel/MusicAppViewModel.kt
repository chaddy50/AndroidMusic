package com.chaddy50.musicapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MusicAppViewModel(
    private val musicScanner: MusicScanner,
    private val trackRepository: TrackRepository,
    private val albumRepository: AlbumRepository,
) : ViewModel() {
    suspend fun getTrackCount(): Int {
        return trackRepository.count()
    }

    fun getAlbumById(albumId: Int): StateFlow<Album?> {
        return albumRepository.getAlbumById(albumId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = null,
            )
    }

    suspend fun refreshLibrary() {
        musicScanner.scan()
    }
}