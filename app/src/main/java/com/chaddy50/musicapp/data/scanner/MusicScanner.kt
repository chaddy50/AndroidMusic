package com.chaddy50.musicapp.data.scanner

import android.content.Context
import android.provider.MediaStore
import com.chaddy50.musicapp.data.api.audioDb.AudioDbRepository
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.AlbumRepository
import com.chaddy50.musicapp.data.repository.ArtistRepository
import com.chaddy50.musicapp.data.repository.ComposerRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.repository.PerformanceRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import com.chaddy50.musicapp.data.scanner.processor.AlbumArtistProcessor
import com.chaddy50.musicapp.data.scanner.processor.AlbumProcessor
import com.chaddy50.musicapp.data.scanner.processor.ArtistProcessor
import com.chaddy50.musicapp.data.scanner.processor.ComposerProcessor
import com.chaddy50.musicapp.data.scanner.processor.GenreProcessor
import com.chaddy50.musicapp.data.scanner.processor.PerformanceProcessor
import com.chaddy50.musicapp.data.scanner.processor.TrackProcessor
import com.chaddy50.musicapp.data.scanner.util.ColumnIndices
import com.chaddy50.musicapp.data.scanner.util.ArtworkSaver
import com.chaddy50.musicapp.data.scanner.util.MetadataResolver
import com.chaddy50.musicapp.data.scanner.util.getDataFromCursor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext

class MusicScanner(
    val context: Context,
    val genreRepository: GenreRepository,
    val artistRepository: ArtistRepository,
    val albumArtistRepository: AlbumArtistRepository,
    val albumRepository: AlbumRepository,
    val trackRepository: TrackRepository,
    val performanceRepository: PerformanceRepository,
    val composerRepository: ComposerRepository,
    val audioDbRepository: AudioDbRepository,
) {
    private val _scanProgress = MutableSharedFlow<Float>()
    val scanProgress = _scanProgress.asSharedFlow()

    private val BATCH_SIZE = 500

    suspend fun scan() {
        withContext(Dispatchers.IO) {
            val metadataResolver = MetadataResolver(context)
            val artworkSaver = ArtworkSaver(context)

            val projection = arrayOf(
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.GENRE,
                MediaStore.Audio.AudioColumns.ALBUM_ID,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.ARTIST_ID,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.ALBUM_ARTIST,
                MediaStore.Audio.AudioColumns.YEAR,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DISC_NUMBER,
                MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER,
                MediaStore.Audio.AudioColumns.DURATION,
            )

            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                "${MediaStore.Audio.AudioColumns.IS_MUSIC} != 0",
                null,
                null
            )?.use { cursor ->
                val numberOfTracksToScan = cursor.count
                var numberOfTracksScanned = 0

                val columns = ColumnIndices(cursor)
                val trackBuffer = mutableListOf<com.chaddy50.musicapp.data.entity.Track>()

                val trackProcessor = TrackProcessor()
                val genreProcessor = GenreProcessor(genreRepository)
                val artistProcessor = ArtistProcessor(artistRepository)
                val albumArtistProcessor = AlbumArtistProcessor(albumArtistRepository, audioDbRepository)
                val albumProcessor = AlbumProcessor(albumRepository, artworkSaver)
                val performanceProcessor = PerformanceProcessor(performanceRepository, artworkSaver)
                val composerProcessor = ComposerProcessor(composerRepository)

                genreProcessor.setUpClassicalMappings()

                while (cursor.moveToNext()) {
                    metadataResolver.resetForNextTrack()
                    val cursorData = getDataFromCursor(cursor, columns)

                    val yearResolver: () -> String = { metadataResolver.getYear(cursorData) }
                    val trackNumber = metadataResolver.getTrackNumber(cursorData)

                    val (genreId, genreName, parentGenreId, isClassical, shouldFetchArtistArtwork) = genreProcessor.process(cursorData)
                    val (artistId, artistName) = artistProcessor.process(cursorData)
                    val (albumArtistId, albumArtistName) = albumArtistProcessor.process(cursorData, genreId, isClassical, shouldFetchArtistArtwork)
                    val (albumId, albumName, albumArtworkPath, albumYear) = albumProcessor.process(cursorData, cursorData.trackId, albumArtistId, yearResolver)
                    val performance = performanceProcessor.process(cursorData, isClassical, cursorData.trackId, genreId, albumId, artistId, yearResolver)
                    composerProcessor.process(isClassical,albumArtistId, albumArtistName)

                    // Always use performance artwork for classical
                    // Even if it doesn't have artwork, we don't want to use artwork from a different performance
                    val artworkPath = if (isClassical) performance?.second else albumArtworkPath
                    // Same for year
                    val year = if (isClassical) performance?.third ?: "Unknown Year" else albumYear

                    trackBuffer.add(
                        trackProcessor.process(
                            cursorData,
                            cursorData.trackId,
                            trackNumber,
                            genreId,
                            genreName,
                            parentGenreId,
                            if (parentGenreId != null) "Classical" else "",
                            artistId,
                            artistName,
                            albumId,
                            albumName,
                            artworkPath,
                            albumArtistId,
                            albumArtistName,
                            performance?.first,
                            year,
                        )
                    )

                    numberOfTracksScanned++
                    _scanProgress.emit(numberOfTracksScanned.toFloat() / numberOfTracksToScan.toFloat())

                    if (trackBuffer.size >= BATCH_SIZE) {
                        trackRepository.insertMultiple(trackBuffer)
                        trackBuffer.clear()
                    }
                }

                metadataResolver.release()
                if (trackBuffer.isNotEmpty()) {
                    trackRepository.insertMultiple(trackBuffer)
                }
            }
        }
    }
}
