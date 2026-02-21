package com.chaddy50.musicapp.data.scanner

import android.content.Context
import android.provider.MediaStore
import com.chaddy50.musicapp.data.entity.Track
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.AlbumRepository
import com.chaddy50.musicapp.data.repository.ArtistRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.repository.PerformanceRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import com.chaddy50.musicapp.data.scanner.processor.AlbumArtistProcessor
import com.chaddy50.musicapp.data.scanner.processor.AlbumProcessor
import com.chaddy50.musicapp.data.scanner.processor.ArtistProcessor
import com.chaddy50.musicapp.data.scanner.processor.GenreProcessor
import com.chaddy50.musicapp.data.scanner.processor.PerformanceProcessor
import com.chaddy50.musicapp.data.scanner.processor.TrackProcessor
import com.chaddy50.musicapp.data.scanner.util.ArtworkSaver
import com.chaddy50.musicapp.data.scanner.util.ColumnIndices
import com.chaddy50.musicapp.data.scanner.util.CursorData
import com.chaddy50.musicapp.data.scanner.util.MetadataResolver
import com.chaddy50.musicapp.data.scanner.util.getDataFromCursor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger
import androidx.core.content.edit

class MusicScanner(
    val context: Context,
    val genreRepository: GenreRepository,
    val artistRepository: ArtistRepository,
    val albumArtistRepository: AlbumArtistRepository,
    val albumRepository: AlbumRepository,
    val trackRepository: TrackRepository,
    val performanceRepository: PerformanceRepository,
) {
    private val _scanProgress = MutableSharedFlow<Float>()
    val scanProgress = _scanProgress.asSharedFlow()

    private val BATCH_SIZE = 500
    private val WORKER_COUNT = Runtime.getRuntime().availableProcessors().coerceAtLeast(2)

    suspend fun scan() {
        withContext(Dispatchers.IO) {
            val prefs = context.getSharedPreferences("music_scanner_prefs", Context.MODE_PRIVATE)
            val lastScanTime = prefs.getLong("last_scan_time", 0L)
            val scanStartTime = System.currentTimeMillis() / 1000

            deleteTracksThatAreNoLongerInFileSystem()

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
                MediaStore.Audio.AudioColumns.DATE_MODIFIED,
            )

            val selection = "${MediaStore.Audio.AudioColumns.IS_MUSIC} != 0 AND " +
                "(${MediaStore.Audio.AudioColumns.DATE_MODIFIED} >= ? OR " +
                "${MediaStore.Audio.AudioColumns.DATE_ADDED} >= ?)"
            val selectionArgs = arrayOf(lastScanTime.toString(), lastScanTime.toString())

            context.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
            )?.use { cursor ->
                val columns = ColumnIndices(cursor)

                // Collect all cursor data sequentially
                val allCursorData = mutableListOf<CursorData>()
                while (cursor.moveToNext()) {
                    allCursorData.add(getDataFromCursor(cursor, columns))
                }

                val totalTracks = allCursorData.size
                if (totalTracks == 0) return@use

                // Distribute tracks round-robin across workers
                // This ensures classical tracks (often contiguous) are spread evenly
                val chunks = Array(WORKER_COUNT) { mutableListOf<CursorData>() }
                for ((index, cursorData) in allCursorData.withIndex()) {
                    chunks[index % WORKER_COUNT].add(cursorData)
                }
                val processedCount = AtomicInteger(0)
                var lastEmittedPercent = -1

                coroutineScope {
                    for (chunk in chunks) {
                        launch {
                            val metadataResolver = MetadataResolver(context)
                            val trackProcessor = TrackProcessor()
                            val genreProcessor = GenreProcessor(genreRepository)
                            val artistProcessor = ArtistProcessor(artistRepository)
                            val albumArtistProcessor = AlbumArtistProcessor(albumArtistRepository)
                            val albumProcessor = AlbumProcessor(albumRepository, artworkSaver)
                            val performanceProcessor = PerformanceProcessor(performanceRepository, artworkSaver)

                            genreProcessor.setUpClassicalMappings()

                            val trackBuffer = mutableListOf<Track>()

                            for (cursorData in chunk) {
                                metadataResolver.resetForNextTrack()

                                val yearResolver: () -> String = { metadataResolver.getYear(cursorData) }
                                val trackNumber = metadataResolver.getTrackNumber(cursorData)

                                val (genreId, genreName, parentGenreId, isClassical) = genreProcessor.process(cursorData)
                                val (artistId, artistName) = artistProcessor.process(cursorData)
                                val (albumArtistId, albumArtistName) = albumArtistProcessor.process(cursorData, genreId)
                                val (albumId, albumName, albumArtworkPath, albumYear) = albumProcessor.process(cursorData, cursorData.trackId, albumArtistId, yearResolver)
                                val performance = performanceProcessor.process(cursorData, isClassical, cursorData.trackId, genreId, albumId, artistId, yearResolver)

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

                                val count = processedCount.incrementAndGet()
                                val currentPercent = (count * 100) / totalTracks
                                if (currentPercent > lastEmittedPercent) {
                                    lastEmittedPercent = currentPercent
                                    _scanProgress.emit(count.toFloat() / totalTracks.toFloat())
                                }

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

            prefs.edit { putLong("last_scan_time", scanStartTime) }
        }
    }

    private suspend fun deleteTracksThatAreNoLongerInFileSystem() {
        // Lightweight query to get all current MediaStore track IDs for deletion detection
        val mediaStoreIds = mutableSetOf<Long>()
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Audio.AudioColumns._ID),
            "${MediaStore.Audio.AudioColumns.IS_MUSIC} != 0",
            null,
            null
        )?.use { cursor ->
            while (cursor.moveToNext()) {
                mediaStoreIds.add(cursor.getLong(0))
            }
        }

        // Remove tracks that no longer exist in MediaStore
        val dbTrackIds = trackRepository.getAllTrackIds()
        val deletedIds = dbTrackIds.filter { it !in mediaStoreIds }
        if (deletedIds.isNotEmpty()) {
            trackRepository.deleteByIds(deletedIds)
        }
    }
}
