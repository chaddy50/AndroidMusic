package com.chaddy50.musicapp.viewModel

import androidx.lifecycle.ViewModel
import com.chaddy50.musicapp.data.MusicScanner
import com.chaddy50.musicapp.data.repository.TrackRepository

class MusicAppViewModel(
    private val musicScanner: MusicScanner,
    private val trackRepository: TrackRepository,
) : ViewModel() {
    suspend fun getTrackCount(): Int {
        return trackRepository.count()
    }

    suspend fun refreshLibrary() {
        musicScanner.scan()
    }
}