package com.chaddy50.musicapp.data

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.util.Size
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.data.entity.Artist
import com.chaddy50.musicapp.data.entity.Performance
import com.chaddy50.musicapp.data.entity.Track
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.AlbumRepository
import com.chaddy50.musicapp.data.repository.ArtistRepository
import com.chaddy50.musicapp.data.repository.GenreMappingRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.repository.PerformanceRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import com.chaddy50.musicapp.utilities.extractCatalogNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.time.DurationUnit
import kotlin.time.toDuration

const val GENRE_CLASSICAL = "Classical"

data class MusicScanner(
    val context: Context,
    val genreRepository: GenreRepository,
    val artistRepository: ArtistRepository,
    val albumArtistRepository: AlbumArtistRepository,
    val albumRepository: AlbumRepository,
    val trackRepository: TrackRepository,
    val genreMappingRepository: GenreMappingRepository,
    val performanceRepository: PerformanceRepository,
) {
    private var genreMappings: Map<String, String> = emptyMap()
    private var parentGenreIdCache: MutableMap<String, Int> = mutableMapOf()
    private var performanceIdCache: MutableMap<Pair<Int, Int>, Int> = mutableMapOf()

    private val BATCH_SIZE = 500

    private val processedArtists = mutableSetOf<Pair<Int, String>>()
    private val processedAlbums = mutableSetOf<Pair<Int, String>>()

    suspend fun scan() {
        setUpGenreMappings()

        withContext(Dispatchers.IO) {
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
                val columns = ColumnIndices(cursor)

                val trackBuffer = mutableListOf<Track>()

                while (cursor.moveToNext()) {
                    val trackId = cursor.getLong(columns.trackId)

                    val (genreId, genreName, parentGenreId) = processGenre(cursor, columns)
                    val (artistId, artistName) = processArtist(cursor, columns)
                    val (albumArtistId, albumArtistName) = processAlbumArtist(cursor, columns, genreId)
                    val (albumId, albumName) =
                        processAlbum(cursor, columns, trackId, albumArtistId)

                    var performanceId: Int? = performanceIdCache[Pair(albumId, artistId)]
                    if (performanceId == null && genreMappings[genreName] == GENRE_CLASSICAL) {
                        performanceId =
                            processPerformance(cursor, columns, trackId, albumId, artistId)
                        performanceIdCache.put(Pair(albumId, artistId), performanceId)
                    }
                    trackBuffer.add(
                        createTrack(
                            cursor,
                            columns,
                            trackId,
                            genreId,
                            genreName,
                            parentGenreId,
                            if (parentGenreId != null) "Classical" else null,
                            artistId,
                            artistName,
                            albumId,
                            albumName,
                            albumArtistId,
                            albumArtistName,
                            performanceId
                        )
                    )

                    if (trackBuffer.size >= BATCH_SIZE) {
                        trackRepository.insertMultiple(trackBuffer)
                        trackBuffer.clear()
                    }
                }

                if (trackBuffer.isNotEmpty()) {
                    trackRepository.insertMultiple(trackBuffer)
                }
            }
        }
    }

    //#region Scan Helpers

    //#region Genres
    private suspend fun setUpGenreMappings() {
        // This is how I should eventually do this once the mappings are configurable by the user
//        genreMappings = genreMappingRepository.getAllMappingsAsMap()
//        val parentGenreNames = genreMappings.values.distinct()
//        for (parentGenreName in parentGenreNames) {
//            val parentId = genreRepository.findOrInsertGenreByName(parentGenreName)
//            parentGenreIdCache[parentGenreName] = parentId
//        }

        // For now, hard coding everything
        genreMappings = mapOf(
            "Solo Piano" to "Classical",
            "Symphony" to "Classical",
            "String Quartet" to "Classical",
            "Piano Concerto" to "Classical",
        )

        val classicalId = genreRepository.findOrInsertGenreByName(GENRE_CLASSICAL)
        parentGenreIdCache[GENRE_CLASSICAL] = classicalId
    }

    private suspend fun processGenre(cursor: Cursor, columns: ColumnIndices): Triple<Int, String, Int?> {
        val genreName = cursor.getStringOrNull(columns.genreName) ?: "Unknown Genre"
        val parentGenreId = getParentGenreId(genreName)

        return Triple(genreRepository.findOrInsertGenreByName(genreName, parentGenreId), genreName, parentGenreId)
    }

    private fun getParentGenreId(genreName: String): Int? {
        val parentGenreName = genreMappings[genreName] ?: return null
        return parentGenreIdCache[parentGenreName]
    }
    //#endregion

    //region Artists
    private suspend fun processArtist(cursor: Cursor, columns: ColumnIndices): Pair<Int, String> {
        val artistId = (cursor.getLongOrNull(columns.artistId) ?: -1).toInt()
        if (processedArtists.any { it.first == artistId }) {
            val artistName = processedArtists.first { it.first == artistId }.second
            return Pair(artistId, artistName)
        }

        val artistName = getArtistName(cursor, columns)
        artistRepository.insert(
            Artist(
                artistId,
                artistName,
            )
        )
        processedArtists.add(Pair(artistId, artistName))
        return Pair(artistId, artistName)
    }
    //#endregion

    //#region Album Artists
    private suspend fun processAlbumArtist(
        cursor: Cursor,
        columns: ColumnIndices,
        genreId: Int,
    ): Pair<Int, String> {
        val albumArtistName = cursor.getStringOrNull(columns.albumArtistName) ?: "Unknown Artist"
        val albumArtistId = albumArtistRepository.findOrInsertAlbumArtist(
            albumArtistName,
            genreId
        )
        return Pair(albumArtistId, albumArtistName)
    }
    //#endregion

    //#region Albums
    private suspend fun processAlbum(
        cursor: Cursor,
        columns: ColumnIndices,
        trackId: Long,
        albumArtistId: Int,
    ): Pair<Int, String> {
        val albumId = (cursor.getLongOrNull(columns.albumId) ?: -1).toInt()
        if (processedAlbums.any { it.first == albumId }) {
            val albumName = processedAlbums.first { it.first == albumId }.second
            return Pair(albumId, albumName)
        }

        val albumName = getAlbumName(cursor, columns)
        val catalogueNumber = extractCatalogNumber(albumName)
        val albumYear = getYear(cursor, columns, trackId)

        val albumArtworkBitmap = getAlbumArtwork(context, trackId)
        val artworkPath = albumArtworkBitmap?.let { bitmap ->
            saveAlbumArtworkToFile(context, bitmap, albumId)
        }
        albumRepository.insert(
            Album(
                albumId,
                albumName,
                catalogueNumber,
                albumArtistId,
                albumYear,
                artworkPath
            )
        )
        processedAlbums.add(Pair(albumId, albumName))
        return Pair(albumId, albumName)
    }

    private fun getAlbumArtwork(
        context: Context,
        trackId: Long
    ): Bitmap? {
        val uri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            trackId
        )

        return try {
            context.contentResolver.loadThumbnail(
                uri,
                Size(800, 800),
                null
            )
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun saveAlbumArtworkToFile(
        context: Context,
        bitmap: Bitmap,
        albumId: Int,
    ): String? {
        val directory = File(context.filesDir, "album_artwork")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, "$albumId.jpg")
        return try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            }
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    //#endregion

    //#region Tracks
    private fun createTrack(
        cursor: Cursor,
        columns: ColumnIndices,
        trackId: Long,
        genreId: Int,
        genreName: String,
        parentGenreId: Int?,
        parentGenreName: String?,
        artistId: Int,
        artistName: String,
        albumId: Int,
        albumName: String,
        albumArtistId: Int,
        albumArtistName: String,
        performanceId: Int?,
    ): Track {
        val trackUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            trackId
        ).toString()
        val trackTitle = cursor.getStringOrNull(columns.trackTitle) ?: "Unknown Title"
        val trackNumber = cursor.getIntOrNull(columns.trackNumber) ?: 0

        val discNumber = cursor.getIntOrNull(columns.discNumber) ?: 0
        val trackDuration = cursor.getIntOrNull(columns.trackDuration) ?: 0
        return Track(
            trackId.toInt(),
            trackUri,
            trackTitle,
            trackNumber,
            albumId,
            albumName,
            artistId,
            artistName,
            albumArtistId,
            albumArtistName,
            genreId,
            genreName,
            parentGenreId,
            parentGenreName,
            trackDuration.toDuration(DurationUnit.MILLISECONDS),
            discNumber,
            performanceId
        )
    }
    //#endregion

    //#region Performance
    private suspend fun processPerformance(
        cursor: Cursor,
        columns: ColumnIndices,
        trackId: Long,
        albumId: Int,
        artistId: Int,
    ): Int {
        val albumName = getAlbumName(cursor, columns)
        val artistName = getArtistName(cursor, columns)
        return performanceRepository.insert(
            Performance(
                0,
                albumId,
                albumName,
                artistId,
                artistName,
                getYear(
                    cursor,
                    columns,
                    trackId
                )
            )
        )
    }
    //#endregion

    private fun getYear(
        cursor: Cursor,
        columns: ColumnIndices,
        trackId: Long,
    ): String {
        var year = cursor.getStringOrNull(columns.year)
        if (year.isNullOrBlank() || year == "0") {
            val retriever = MediaMetadataRetriever()
            try {
                setRetrieverSource(retriever, trackId)
                year = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
            } finally {
                retriever.release()
            }
        }
        if (year.isNullOrBlank() || year == "0") {
            year = "Unknown Year"
        }
        if (year != "Unknown Year") {
            year = year.take(4)
        }
        return year
    }

    private fun getAlbumName(
        cursor: Cursor,
        columns: ColumnIndices,
    ): String {
        return cursor.getStringOrNull(columns.albumName) ?: "Unknown Album"
    }

    private fun getArtistName(
        cursor: Cursor,
        columns: ColumnIndices,
    ): String {
        return cursor.getStringOrNull(columns.artistName) ?: "Unknown Artist"
    }

    private data class ColumnIndices(
        val trackId: Int,
        val trackTitle: Int,
        val artistId: Int,
        val artistName: Int,
        val albumId: Int,
        val albumName: Int,
        val albumArtistName: Int,
        val discNumber: Int,
        val trackNumber: Int,
        val trackDuration: Int,
        val year: Int,
        val genreName: Int
    ) {
        constructor(cursor: Cursor) : this(
            trackId = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID),
            trackTitle = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE),
            artistId = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST_ID),
            artistName = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST),
            albumId = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID),
            albumName = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM),
            albumArtistName = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ARTIST),
            discNumber = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISC_NUMBER),
            trackNumber = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER),
            trackDuration = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION),
            year = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.YEAR),
            genreName = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.GENRE)
        )
    }

    private fun setRetrieverSource(
        retriever: MediaMetadataRetriever,
        trackId: Long
    ) {
        val contentUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            trackId
        )
        context.contentResolver.openAssetFileDescriptor(contentUri, "r")?.use { afd ->
            retriever.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
        }
    }
    //#endregion
}