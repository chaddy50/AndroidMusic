package com.chaddy50.froh.ui.screens.genresScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaddy50.froh.data.ClassicalGenreConfig
import com.chaddy50.froh.data.entity.Genre
import com.chaddy50.froh.data.repository.AlbumArtistRepository
import com.chaddy50.froh.data.repository.AlbumRepository
import com.chaddy50.froh.data.repository.GenreRepository
import com.chaddy50.froh.utilities.chooseAlbumLabel
import com.chaddy50.froh.utilities.chooseArtistLabel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class GenreWithStats(
    val genre: Genre,
    val subtitle: String,
)

data class GenresScreenUiState(
    val screenTitle: String = "Genres",
    val genres: List<GenreWithStats> = emptyList(),
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class GenresScreenViewModel @Inject constructor(
    genreRepository: GenreRepository,
    albumArtistRepository: AlbumArtistRepository,
    albumRepository: AlbumRepository,
    private val classicalGenreConfig: ClassicalGenreConfig,
) : ViewModel() {
    val uiState: StateFlow<GenresScreenUiState> = genreRepository.getAllTopLevelGenres()
        .flatMapLatest { genres ->
            if (genres.isEmpty()) {
                flowOf(emptyList())
            } else {
                val countFlows = genres.map { genre ->
                    val isClassical = genre.id == classicalGenreConfig.classicalGenreId
                    val artistLabel = chooseArtistLabel(isClassical)
                    val albumLabel = chooseAlbumLabel(isClassical)
                    combine(
                        albumArtistRepository.getNumberOfAlbumArtistsForGenre(genre.id),
                        albumRepository.getNumberOfAlbumsForGenre(genre.id),
                    ) { artistCount, albumCount ->
                        GenreWithStats(genre, "$artistCount $artistLabel \u00B7 $albumCount $albumLabel")
                    }
                }
                combine(countFlows) { it.toList() }
            }
        }
        .map { genresWithStats ->
            GenresScreenUiState("Genres", genresWithStats, false)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GenresScreenUiState(isLoading = true)
        )
}
