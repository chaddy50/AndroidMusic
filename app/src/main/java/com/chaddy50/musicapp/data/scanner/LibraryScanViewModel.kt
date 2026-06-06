package com.chaddy50.musicapp.data.scanner

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaddy50.musicapp.MusicApplication
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryScanViewModel @Inject constructor(
    private val application: Application,
    private val musicScanner: MusicScanner,
    private val trackRepository: TrackRepository,
    private val genreRepository: GenreRepository,
) : ViewModel() {
    private val _isScanInProgress = MutableStateFlow(false)
    val isScanInProgress = _isScanInProgress.asStateFlow()

    private val _scanProgress = MutableStateFlow(0f)
    val scanProgress = _scanProgress.asStateFlow()

    init {
        viewModelScope.launch {
            initializeClassicalGenreId()

            musicScanner.scanProgress.collect {
                _scanProgress.value = it
            }
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
        (application as MusicApplication).classicalGenreId =
            genreRepository.getGenreByName("Classical")?.id
    }
}
