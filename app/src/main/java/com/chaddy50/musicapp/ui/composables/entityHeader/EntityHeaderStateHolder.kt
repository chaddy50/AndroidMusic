package com.chaddy50.musicapp.ui.composables.entityHeader

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.chaddy50.musicapp.MusicApplication
import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.AlbumRepository
import com.chaddy50.musicapp.data.repository.ArtistRepository
import com.chaddy50.musicapp.data.repository.ComposerRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.repository.PerformanceRepository
import com.chaddy50.musicapp.data.repository.PlaylistRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import com.chaddy50.musicapp.data.scanner.processor.shouldFetchArtistArtworkForGenre
import com.chaddy50.musicapp.utilities.formatMillisecondsIntoMinutesAndSeconds
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
    private val genreId: Long?,
    private val albumArtistId: Long?,
    private val albumId: Long?,
    private val performanceId: Long?,
    private val playlistId: Long?,
    private val classicalGenreId: Long?,
    private val genreRepository: GenreRepository,
    private val albumArtistRepository: AlbumArtistRepository,
    private val artistRepository: ArtistRepository,
    private val albumRepository: AlbumRepository,
    private val trackRepository: TrackRepository,
    private val performanceRepository: PerformanceRepository,
    private val composerRepository: ComposerRepository,
    private val playlistRepository: PlaylistRepository,
    private val coroutineScope: CoroutineScope,
) {
    var uiState: StateFlow<EntityHeaderState>

    init {
        val stateFlow = when (type) {
            EntityType.Album -> getStateForAlbum()
            EntityType.Genre -> getStateForGenre()
            EntityType.AlbumArtist -> getStateForAlbumArtist()
            EntityType.Playlist -> getStateForPlaylist()
            else -> flowOf(EntityHeaderState(isLoading = false))
        }
            uiState = stateFlow.stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5_000),
            EntityHeaderState()
        )
    }

    private fun getStateForAlbum(): Flow<EntityHeaderState> {
        if (albumId == null) {
            return flowOf(EntityHeaderState())
        }

        if (performanceId != null) {
            return combine(
                albumRepository.getAlbumById(albumId),
                performanceRepository.getPerformanceById(performanceId),
                trackRepository.getTracksForPerformance(performanceId),
            ) { album, performance, tracks ->
                val tracksLabel = if (genreId == classicalGenreId) "movements" else "tracks"
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

        return albumRepository.getAlbumById(albumId).flatMapLatest { album ->
            if (album == null) {
                return@flatMapLatest flowOf(EntityHeaderState())
            }

            combine(
                albumArtistRepository.getAlbumArtistById(album.artistId),
                trackRepository.getTracksForAlbum(albumId),
                performanceRepository.getNumberOfPerformancesForAlbum(albumId),
                playlistRepository.getPlaylistIdsContainingAlbum(album.id)
            ) { albumArtist, tracks, numberOfPerformances, playlistsThatAlbumIsAlreadyIn ->
                val albumDurationMs = tracks.sumOf { it.duration.inWholeMilliseconds }
                EntityHeaderState(
                    album.title,
                    albumArtist?.name ?: "Unknown Artist",
                    getDetailsForAlbumNoPerformance(album, tracks.size, numberOfPerformances, albumDurationMs),
                    if (genreId == classicalGenreId) null else album.artworkPath,
                    false,
                    playlistsThatAlbumIsAlreadyIn
                )
            }
        }
    }

    private fun getDetailsForAlbumNoPerformance(
        album: Album,
        numberOfTracks: Int,
        numberOfPerformances: Int,
        albumDurationMs: Long,
    ): String {
        val isClassical = genreId == classicalGenreId

        var items: List<String> = listOf()
        if (!isClassical) {
            items = items.plus(album.year)
        }
        if (isClassical) {
            items = items.plus("$numberOfPerformances performances")
        } else {
            items = items.plus("$numberOfTracks tracks")
        }
        if (!isClassical) {
            items = items.plus("${formatMillisecondsIntoMinutesAndSeconds(albumDurationMs)}")
        }
        return items.joinToString(" - ")
    }

    private fun getStateForGenre(): Flow<EntityHeaderState> {
        if (genreId == null) {
            return flowOf(EntityHeaderState())
        }
        return combine(
            genreRepository.getGenreById(genreId),
            albumArtistRepository.getNumberOfAlbumArtistsForGenre(genreId),
            playlistRepository.getPlaylistIdsContainingGenre(genreId)
        ) { genre, numberOfAlbumArtists, playlistsThatGenreIsAlreadyIn ->
            val artistLabel = if (genre?.id == classicalGenreId) "composers" else "artists"
            EntityHeaderState(
                genre?.name ?: "Genre",
                "$numberOfAlbumArtists $artistLabel",
                null,
                null,
                false,
                playlistsThatGenreIsAlreadyIn
            )
        }
    }

    private fun getStateForAlbumArtist(): Flow<EntityHeaderState> {
        if (albumArtistId == null || genreId == null) {
            return flowOf(EntityHeaderState())
        }

        return combine(
            albumArtistRepository.getAlbumArtistById(albumArtistId),
            genreRepository.getGenreById(genreId),
            albumRepository.getNumberOfAlbumsForAlbumArtist(albumArtistId),
            composerRepository.getComposerForAlbumArtist(albumArtistId),
            playlistRepository.getPlaylistIdsContainingAlbumArtist(albumArtistId)
        ) { albumArtist, genre, numberOfAlbums, composer, playlistsThatAlbumArtistIsAlreadyIn ->
            val isClassical = genre?.id == classicalGenreId
            val albumsLabel = if (isClassical) "works" else "albums"

            if (isClassical && albumArtist != null && composer == null) {
                coroutineScope.launch(Dispatchers.IO) {
                    composerRepository.fetchAndInsertComposer(
                        albumArtistId,
                        albumArtist.name,
                    )
                }
            }

            if (
                !isClassical
                && albumArtist?.portraitPath == null
                && albumArtist != null
                && shouldFetchArtistArtworkForGenre(genre?.name)
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
                    false,
                    playlistsThatAlbumArtistIsAlreadyIn
                )
            } else {
                EntityHeaderState(
                    albumArtist?.name ?: "Artist",
                    genre?.name ?: "Genre",
                    "$numberOfAlbums $albumsLabel",
                    albumArtist?.portraitPath,
                    false,
                    playlistsThatAlbumArtistIsAlreadyIn
                )
            }
        }
    }

    private fun getStateForPlaylist(): Flow<EntityHeaderState> {
        if (playlistId == null) {
            return flowOf(EntityHeaderState(isLoading = false))
        }
        return combine(
            playlistRepository.getPlaylistById(playlistId),
            playlistRepository.getTracksForPlaylist(playlistId),
        ) { playlist, tracks ->
            val playlistDurationMs = tracks.sumOf { it.duration.inWholeMilliseconds }
            EntityHeaderState(
                playlist?.name ?: "Playlist",
                "${tracks.size} tracks - ${formatMillisecondsIntoMinutesAndSeconds(playlistDurationMs)}",
                null,
                null,
                false,
            )
        }
    }
}

@Composable
fun rememberEntityHeaderState(
    type: EntityType,
    genreId: Long? = null,
    albumArtistId: Long? = null,
    albumId: Long? = null,
    performanceId: Long? = null,
    playlistId: Long? = null,
    classicalGenreId: Long? = null,
    app: MusicApplication = LocalContext.current.applicationContext as MusicApplication,
    genreRepository: GenreRepository = app.genreRepository,
    albumArtistRepository: AlbumArtistRepository = app.albumArtistRepository,
    artistRepository: ArtistRepository = app.artistRepository,
    albumRepository: AlbumRepository = app.albumRepository,
    trackRepository: TrackRepository = app.trackRepository,
    performanceRepository: PerformanceRepository = app.performanceRepository,
    composerRepository: ComposerRepository = app.composerRepository,
    playlistRepository: PlaylistRepository = app.playlistRepository,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): EntityHeaderStateHolder {
    return remember(
        type,
        genreId,
        albumArtistId,
        albumId,
        performanceId,
        playlistId,
        classicalGenreId,
    ) {
        EntityHeaderStateHolder(
            type,
            genreId,
            albumArtistId,
            albumId,
            performanceId,
            playlistId,
            classicalGenreId,
            genreRepository,
            albumArtistRepository,
            artistRepository,
            albumRepository,
            trackRepository,
            performanceRepository,
            composerRepository,
            playlistRepository,
            coroutineScope
        )
    }
}
