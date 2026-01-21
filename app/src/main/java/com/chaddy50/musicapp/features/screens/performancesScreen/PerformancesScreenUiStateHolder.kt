package com.chaddy50.musicapp.features.screens.performancesScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.chaddy50.musicapp.MusicApplication
import com.chaddy50.musicapp.data.entity.Performance
import com.chaddy50.musicapp.data.repository.PerformanceRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Stable
class PerformancesScreenUiStateHolder(
    albumId: Int?,
    performanceRepository: PerformanceRepository,
    coroutineScope: CoroutineScope
) {
    var uiState: StateFlow<PerformanceScreenUiState>

    init {
        var performances: Flow<List<Performance>> = flowOf(emptyList())
        if (albumId != null) {
            performances = performanceRepository.getPerformancesForAlbum(albumId)
        }

        uiState = performances.map { performances ->
            PerformanceScreenUiState(
                "Performances",
                performances,
                false,
            )
        }.stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5_000),
            PerformanceScreenUiState(isLoading = true)
        )
    }
}

@Composable
fun rememberPerformancesScreenState(
    albumId: Int?,
    app: MusicApplication = LocalContext.current.applicationContext as MusicApplication,
    performanceRepository: PerformanceRepository = app.performanceRepository,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
): PerformancesScreenUiStateHolder {
    return remember(albumId, performanceRepository, coroutineScope) {
        PerformancesScreenUiStateHolder(
            albumId,
            performanceRepository,
            coroutineScope
        )
    }
}