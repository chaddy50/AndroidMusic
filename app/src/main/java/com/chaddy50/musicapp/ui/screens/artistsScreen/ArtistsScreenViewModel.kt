package com.chaddy50.musicapp.ui.screens.artistsScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.chaddy50.musicapp.data.ClassicalGenreConfig
import com.chaddy50.musicapp.data.entity.AlbumArtist
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.ComposerRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.repository.PlaylistRepository
import com.chaddy50.musicapp.data.scanner.processor.shouldFetchArtistArtworkForGenre
import com.chaddy50.musicapp.navigation.ArtistsRoute
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityHeaderState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArtistsScreenUiState(
    val screenTitle: String = "Artists",
    val artists: List<AlbumArtist> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class ArtistsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    classicalGenreConfig: ClassicalGenreConfig,
    albumArtistRepository: AlbumArtistRepository,
    genreRepository: GenreRepository,
    playlistRepository: PlaylistRepository,
    composerRepository: ComposerRepository,
) : ViewModel() {
    val uiState: StateFlow<ArtistsScreenUiState>
    val entityHeaderState: StateFlow<EntityHeaderState>

    init {
        val route = savedStateHandle.toRoute<ArtistsRoute>()
        val genreId = route.genreId
        val classicalGenreId = classicalGenreConfig.classicalGenreId

        val artistsToShow: Flow<List<AlbumArtist>> =
            albumArtistRepository.getAlbumArtistsForGenre(genreId)

        val genreName: Flow<String?> = genreRepository.getGenreName(genreId)

        uiState = combine(artistsToShow, genreName) { artists, name ->
            ArtistsScreenUiState(
                screenTitle = name ?: "Artists",
                artists = artists,
                isLoading = false,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ArtistsScreenUiState(isLoading = true),
        )

        entityHeaderState = combine(
            genreRepository.getGenreById(genreId),
            albumArtistRepository.getNumberOfAlbumArtistsForGenre(genreId),
            playlistRepository.getPlaylistIdsContainingGenre(genreId),
        ) { genre, numberOfAlbumArtists, playlistsThatGenreIsAlreadyIn ->
            val artistLabel = if (genre?.id == classicalGenreId) "composers" else "artists"
            EntityHeaderState(
                genre?.name ?: "Genre",
                "$numberOfAlbumArtists $artistLabel",
                null,
                null,
                false,
                playlistsThatGenreIsAlreadyIn,
            )
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            EntityHeaderState(),
        )

        viewModelScope.launch(Dispatchers.IO) {
            val artists = artistsToShow.first()
            val genre = genreRepository.getGenreById(genreId).first()
            val isClassical = genreId == classicalGenreId

            for (artist in artists) {
                if (artist.portraitPath != null) continue
                try {
                    if (isClassical) {
                        val composer = composerRepository.getComposerForAlbumArtist(artist.id).first()
                        if (composer == null) {
                            composerRepository.fetchAndInsertComposer(artist.id, artist.name)
                        }
                    } else if (shouldFetchArtistArtworkForGenre(genre?.name)) {
                        albumArtistRepository.fetchAndUpdatePortrait(artist)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}
