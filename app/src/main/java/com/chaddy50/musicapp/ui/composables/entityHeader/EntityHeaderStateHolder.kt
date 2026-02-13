package com.chaddy50.musicapp.ui.composables.entityHeader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.chaddy50.musicapp.MusicApplication
import com.chaddy50.musicapp.data.GENRES_WITHOUT_ARTIST_ARTWORK
import com.chaddy50.musicapp.data.api.audioDb.AudioDbRepository
import com.chaddy50.musicapp.data.api.openOpus.OpenOpusRepository
import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.AlbumRepository
import com.chaddy50.musicapp.data.repository.ArtistRepository
import com.chaddy50.musicapp.data.repository.ComposerRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.repository.PerformanceRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import com.chaddy50.musicapp.data.util.ArtworkDownloader
import com.chaddy50.musicapp.utilities.formatMillisecondsIntoMinutesAndSeconds
import com.chaddy50.musicapp.viewModel.MusicAppViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
    private val composerRepository: ComposerRepository,
    private val openOpusRepository: OpenOpusRepository,
    private val audioDbRepository: AudioDbRepository,
    private val artworkDownloader: ArtworkDownloader,
    private val coroutineScope: CoroutineScope,
) {
    var uiState: StateFlow<EntityHeaderState>

    init {
        val stateFlow = when (type) {
            EntityType.Album -> getStateForAlbum()
            EntityType.Genre -> getStateForGenre()
            EntityType.AlbumArtist -> getStateForAlbumArtist()
            EntityType.SubGenre -> getStateForSubGenre()
            EntityType.All -> getStateForAllMusic()
            else -> flowOf(EntityHeaderState(isLoading = false))
        }
            uiState = stateFlow.stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5_000),
            EntityHeaderState()
        )
    }

    private fun getStateForAllMusic(): Flow<EntityHeaderState> {
        return combine(
            genreRepository.getNumberOfTopLevelGenres(),
            albumArtistRepository.getNumberOfAlbumArtists(),
            albumRepository.getNumberOfAlbums(),
            trackRepository.getNumberOfTracks(),
        ) { numberOfTopLevelGenres, numberOfAlbumArtists, numberOfAlbums, numberOfTracks ->
            AllMusicStats(numberOfTopLevelGenres, numberOfAlbumArtists, numberOfAlbums, numberOfTracks)
        }.flatMapLatest { (numberOfTopLevelGenres, numberOfAlbumArtists, numberOfAlbums, numberOfTracks) ->
            flowOf(
                EntityHeaderState(
                    "All Music",
                    "$numberOfTopLevelGenres genres - $numberOfAlbumArtists artists - $numberOfAlbums albums - $numberOfTracks tracks",
                    "",
                    null,
                    false
                )
            )
        }
    }

    data class AllMusicStats(
        val numberOfTopLevelGenres: Int,
        val numberOfAlbumArtists : Int,
        val numberOfAlbums: Int,
        val numberOfTracks: Int,
    )

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
                    performanceRepository.getPerformanceById(selectedPerformanceId),
                    trackRepository.getTracksForPerformance(selectedPerformanceId)
                ) { album, performance, tracks ->
                    val tracksLabel = if (selectedGenreId == viewModel.classicalGenreId) "movements" else "tracks"
                    val performanceDurationMs = tracks.sumOf { it.duration.inWholeMilliseconds }
                    EntityHeaderState(
                        album?.title ?: "Album",
                        "${performance.year} - ${performance.artistName}",
                        "${tracks.size} $tracksLabel - ${formatMillisecondsIntoMinutesAndSeconds(performanceDurationMs)}",
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

                    combine(
                        albumArtistRepository.getAlbumArtistById(album.artistId),
                        trackRepository.getTracksForAlbum(selectedAlbumId),
                        performanceRepository.getNumberOfPerformancesForAlbum(selectedAlbumId)
                    ) { albumArtist, tracks, numberOfPerformances ->
                        val albumDurationMs = tracks.sumOf { it.duration.inWholeMilliseconds }
                        EntityHeaderState(
                            album.title,
                            albumArtist?.name ?: "Unknown Artist",
                            getDetailsForAlbumNoPerformance(selectedGenreId, album, tracks.size, numberOfPerformances, albumDurationMs),
                            if (selectedGenreId == viewModel.classicalGenreId) null else album.artworkPath,
                            false
                        )
                    }
                }
            }
        }
    }

    private fun getDetailsForAlbumNoPerformance(
        genreId: Int?,
        album: Album,
        numberOfTracks: Int,
        numberOfPerformances: Int,
        albumDurationMs: Long,
    ): String {
        var subtitle = album.year
        if (genreId == viewModel.classicalGenreId) {
            subtitle += " - $numberOfPerformances performances"
        } else {
            subtitle += " - $numberOfTracks tracks"
        }
        if (genreId != viewModel.classicalGenreId) {
            subtitle += " - ${formatMillisecondsIntoMinutesAndSeconds(albumDurationMs)}"
        }
        return subtitle
    }

    private fun getStateForGenre(): Flow<EntityHeaderState> {
        return viewModel.selectedGenreId.flatMapLatest { selectedGenreId ->
            if (selectedGenreId == null) {
                return@flatMapLatest flowOf(EntityHeaderState())
            }
            combine(
            genreRepository.getGenreById(selectedGenreId),
                albumArtistRepository.getNumberOfAlbumArtistsForGenre(selectedGenreId)
            ) { genre, numberOfAlbumArtists ->
                val artistLabel = if (genre?.id == viewModel.classicalGenreId) "composers" else "artists"
                EntityHeaderState(
                    genre?.name ?: "Genre",
                    "$numberOfAlbumArtists $artistLabel",
                    null,
                    null,
                    false
                )
            }
        }
    }

    private fun getStateForAlbumArtist(): Flow<EntityHeaderState> {
        return combine(
            viewModel.selectedAlbumArtistId,
            viewModel.selectedGenreId
        ) { selectedAlbumArtistId, selectedGenreId ->
            Pair(selectedAlbumArtistId, selectedGenreId)
        }.flatMapLatest { (selectedAlbumArtistId, selectedGenreId) ->
            if (selectedAlbumArtistId == null) {
                return@flatMapLatest flowOf(EntityHeaderState())
            }
            if (selectedGenreId == null) {
                return@flatMapLatest flowOf(EntityHeaderState())
            }

            combine(
                albumArtistRepository.getAlbumArtistById(selectedAlbumArtistId),
                genreRepository.getGenreById(selectedGenreId),
                albumRepository.getNumberOfAlbumsForAlbumArtist(selectedAlbumArtistId),
                composerRepository.getComposerForAlbumArtist(selectedAlbumArtistId),
            ) { albumArtist, genre, numberOfAlbums, composer ->
                val isClassical = genre?.id == viewModel.classicalGenreId
                val albumsLabel = if (isClassical) "works" else "albums"

                if (isClassical && albumArtist != null && composer == null) {
                    coroutineScope.launch(Dispatchers.IO) {
                        composerRepository.fetchAndInsertComposer(
                            selectedAlbumArtistId,
                            albumArtist.name,
                        )
                    }
                }

                if (
                    !isClassical
                    && albumArtist?.portraitPath == null
                    && albumArtist != null
                    && genre?.name !in GENRES_WITHOUT_ARTIST_ARTWORK
                    ) {
                    coroutineScope.launch(Dispatchers.IO) {
                        albumArtistRepository.fetchAndUpdatePortrait(albumArtist)
                    }
                }

                if (composer != null) {
                    val lifespan = listOfNotNull(composer.birthYear, composer.deathYear)
                        .joinToString("–")
                    val subtitle = listOfNotNull(composer.epoch, lifespan.ifEmpty { null })
                        .joinToString(" - ")

                    EntityHeaderState(
                        composer.completeName,
                        subtitle,
                        "$numberOfAlbums $albumsLabel",
                        composer.portraitPath,
                        false
                    )
                } else {
                    EntityHeaderState(
                        albumArtist?.name ?: "Artist",
                        genre?.name ?: "Genre",
                        "$numberOfAlbums $albumsLabel",
                        albumArtist?.portraitPath,
                        false
                    )
                }
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
                genreRepository.getGenreById(selectedSubGenreId),
                albumRepository.getNumberOfAlbumsForAlbumArtistInGenre(selectedAlbumArtistId, selectedSubGenreId)
            ) { albumArtist, subGenre, numberOfPieces ->
                EntityHeaderState(
                    "${albumArtist?.name} - ${subGenre?.name}",
                    "$numberOfPieces pieces",
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
    composerRepository: ComposerRepository = app.composerRepository,
    openOpusRepository: OpenOpusRepository = app.openOpusRepository,
    audioDbRepository: AudioDbRepository = app.audioDbRepository,
    artworkDownloader: ArtworkDownloader = app.artworkDownloader,
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
        composerRepository,
        openOpusRepository,
        audioDbRepository,
        artworkDownloader,
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
            composerRepository,
            openOpusRepository,
            audioDbRepository,
            artworkDownloader,
            coroutineScope
        )
    }
}