package com.chaddy50.musicapp.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chaddy50.musicapp.MusicApplication

class MusicAppViewModelFactory(private val application: MusicApplication) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MusicAppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MusicAppViewModel(
                musicScanner = application.musicScanner,
                trackRepository = application.trackRepository,
                genreRepository = application.genreRepository,
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}