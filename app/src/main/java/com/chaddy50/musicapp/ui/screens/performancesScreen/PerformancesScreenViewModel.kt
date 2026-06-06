package com.chaddy50.musicapp.ui.screens.performancesScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.chaddy50.musicapp.data.entity.Performance
import com.chaddy50.musicapp.data.repository.AlbumRepository
import com.chaddy50.musicapp.data.repository.PerformanceRepository
import com.chaddy50.musicapp.navigation.PerformancesRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class PerformanceScreenUiState(
    val screenTitle: String = "Performance",
    val performances: List<Performance> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class PerformancesScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    performanceRepository: PerformanceRepository,
    albumRepository: AlbumRepository,
) : ViewModel() {
    val uiState: StateFlow<PerformanceScreenUiState>

    init {
        val route = savedStateHandle.toRoute<PerformancesRoute>()
        val albumId = route.albumId

        val albumTitle: Flow<String> = albumRepository.getAlbumById(albumId)
            .filterNotNull()
            .map { it.title }

        val performances: Flow<List<Performance>> =
            performanceRepository.getPerformancesForAlbum(albumId)

        uiState = combine(performances, albumTitle) { performances, albumTitle ->
            PerformanceScreenUiState(albumTitle, performances, false)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            PerformanceScreenUiState(isLoading = true),
        )
    }
}
