package com.chaddy50.musicapp.features.screens.genresScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.chaddy50.musicapp.MusicApplication
import com.chaddy50.musicapp.data.repository.GenreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Stable
class GenresScreenUiStateHolder(
    genreRepository: GenreRepository,
    coroutineScope: CoroutineScope,
) {
    var uiState: StateFlow<SubGenresScreenUiState>

    init {
        val genres = genreRepository.getAllTopLevelGenres()

        uiState = genres.map { genres ->
            SubGenresScreenUiState(
                "Genres",
                genres,
                false
            )
        }.stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SubGenresScreenUiState(isLoading = true)
        )
    }
}

@Composable
fun rememberGenresScreenState(
    app: MusicApplication = LocalContext.current.applicationContext as MusicApplication,
    genreRepository: GenreRepository = app.genreRepository,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): GenresScreenUiStateHolder {
    return remember(genreRepository, coroutineScope) {
        GenresScreenUiStateHolder(
            genreRepository,
            coroutineScope
        )
    }
}