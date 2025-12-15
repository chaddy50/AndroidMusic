package com.chaddy50.musicapp.ui.composables.entityHeader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.chaddy50.musicapp.MusicApplication
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.AlbumRepository
import com.chaddy50.musicapp.data.repository.ArtistRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.repository.PerformanceRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import com.chaddy50.musicapp.viewModel.MusicAppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
@Stable
class EntityHeaderStateHolder(
    type: EntityType,
    private val viewModel: MusicAppViewModel,
    private val genreRepository: GenreRepository,
    private val albumArtistRepository: AlbumArtistRepository,
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
    private val trackRepository: TrackRepository,
    private val performanceRepository: PerformanceRepository,
    coroutineScope: CoroutineScope,
) {
    var uiState: StateFlow<EntityHeaderState>

    init {
        val stateFlow = when (type) {
            EntityType.Album -> getStateForAlbum()
            EntityType.Genre -> getStateForGenre()
            EntityType.AlbumArtist -> getStateForAlbumArtist()
            EntityType.SubGenre -> getStateForSubGenre()
            EntityType.All -> flowOf(
                EntityHeaderState(
                    "All Music",
                    "",
                    null,
                    null,
                    false
                )
            )
            else -> flowOf(EntityHeaderState(isLoading = false))
        }
            uiState = stateFlow.stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5_000),
            EntityHeaderState()
        )
    }

    private fun getStateForAlbum(): Flow<EntityHeaderState> {
        return combine(
            viewModel.selectedAlbumId,
            viewModel.selectedPerformanceId,
            viewModel.selectedGenreId
        ) { selectedAlbumId, selectedPerformanceId, selectedGenreId ->
            Triple(selectedAlbumId,selectedPerformanceId, selectedGenreId)
        }.flatMapLatest { (selectedAlbumId, selectedPerformanceId, selectedGenreId) ->
            if (selectedAlbumId == null) {
                return@flatMapLatest flowOf(EntityHeaderState())
            }

            if (selectedPerformanceId != null) {
                combine(
                    albumRepository.getAlbumById(selectedAlbumId),
                    performanceRepository.getPerformanceById(selectedPerformanceId)
                ) { album, performance ->
                    EntityHeaderState(
                        album?.title ?: "Album",
                        "${performance?.year} - ${performance.artistName}",
                        null,
                        null,
                        false
                    )
                }
            }
            else {
                albumRepository.getAlbumById(selectedAlbumId).flatMapLatest { album ->
                    if (album == null) {
                        return@flatMapLatest flowOf(EntityHeaderState())
                    }
                    albumArtistRepository.getAlbumArtistById(album.artistId).map { albumArtist ->
                        EntityHeaderState(
                            album.title,
                            albumArtist?.name ?: "Unknown Artist",
                            album.year,
                            if (selectedGenreId == viewModel.classicalGenreId) null else album.artworkPath,
                            false
                        )
                    }
                }
            }
        }
    }

    private fun getStateForGenre(): Flow<EntityHeaderState> {
        return viewModel.selectedGenreId.flatMapLatest { selectedGenreId ->
            if (selectedGenreId == null) {
                return@flatMapLatest flowOf(EntityHeaderState())
            }
            genreRepository.getGenreById(selectedGenreId).map { genre ->
                EntityHeaderState(
                    genre?.name ?: "Genre",
                    "",
                    null,
                    null,
                    false
                )
            }
        }
    }

    private fun getStateForAlbumArtist(): Flow<EntityHeaderState> {
        return viewModel.selectedAlbumArtistId.flatMapLatest { selectedAlbumArtistId ->
            if (selectedAlbumArtistId == null) {
                return@flatMapLatest flowOf(EntityHeaderState())
            }
            albumArtistRepository.getAlbumArtistById(selectedAlbumArtistId).map { albumArtist ->
                EntityHeaderState(
                    albumArtist?.name ?: "Artist",
                    "",
                    null,
                    null,
                    false
                )
            }
        }
    }

    private fun getStateForSubGenre(): Flow<EntityHeaderState> {
        return combine(
            viewModel.selectedAlbumArtistId,
            viewModel.selectedSubGenreId
        ) { selectedAlbumArtistId, selectedSubGenreId ->
            Pair(selectedAlbumArtistId, selectedSubGenreId)
        }.flatMapLatest { (selectedAlbumArtistId, selectedSubGenreId) ->
            if (selectedAlbumArtistId == null || selectedSubGenreId == null) {
                return@flatMapLatest flowOf(EntityHeaderState())
            }

            combine(
                albumArtistRepository.getAlbumArtistById(selectedAlbumArtistId),
                genreRepository.getGenreById(selectedSubGenreId)
            ) { albumArtist, subGenre ->
                EntityHeaderState(
                    "${albumArtist?.name} - ${subGenre?.name}",
                    "",
                    null,
                    null,
                    false
                )
            }
        }
    }
}

@Composable
fun rememberEntityHeaderState(
    type: EntityType,
    viewModel: MusicAppViewModel,
    app: MusicApplication = LocalContext.current.applicationContext as MusicApplication,
    genreRepository: GenreRepository = app.genreRepository,
    albumArtistRepository: AlbumArtistRepository = app.albumArtistRepository,
    artistRepository: ArtistRepository = app.artistRepository,
    albumRepository: AlbumRepository = app.albumRepository,
    trackRepository: TrackRepository = app.trackRepository,
    performanceRepository: PerformanceRepository = app.performanceRepository,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): EntityHeaderStateHolder {
    return remember(
        type,
        viewModel,
        genreRepository,
        albumArtistRepository,
        artistRepository,
        albumRepository,
        trackRepository,
        performanceRepository,
        coroutineScope
    ) {
        EntityHeaderStateHolder(
            type,
            viewModel,
            genreRepository,
            albumArtistRepository,
            artistRepository,
            albumRepository,
            trackRepository,
            performanceRepository,
            coroutineScope
        )
    }
}