package com.chaddy50.musicapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaddy50.musicapp.data.MusicScanner
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MusicAppViewModel(
    private val musicScanner: MusicScanner,
    private val trackRepository: TrackRepository,
    private val genreRepository: GenreRepository,
) : ViewModel() {
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

    init {
        viewModelScope.launch {
            initializeClassicalGenreId()
        }
    }

    var classicalGenreId: Int? = null

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
        return trackRepository.count()
    }

    suspend fun refreshLibrary() {
        musicScanner.scan()
        initializeClassicalGenreId()
    }

    private suspend fun initializeClassicalGenreId() {
        classicalGenreId = genreRepository.getGenreByName("Classical")?.id
    }
}