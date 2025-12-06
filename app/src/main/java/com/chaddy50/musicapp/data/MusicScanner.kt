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

                while (cursor.moveToNext()) {
                    val trackId = cursor.getLong(columns.trackId)
                    val retriever = getMediaMetadataRetriever(trackId)

                    val (genreId, genreName) = processGenre(cursor, columns)
                    val artistId = processArtist(cursor, columns)
                    val albumArtistId = processAlbumArtist(cursor, columns, retriever, genreId)
                    val albumId = processAlbum(cursor, columns, retriever, trackId, albumArtistId)

                    var performanceId: Int? = performanceIdCache[Pair(albumId, artistId)]
                    if (performanceId == null && genreMappings[genreName] == GENRE_CLASSICAL) {
                        performanceId = processPerformance(cursor, columns, retriever, albumId, artistId)
                        performanceIdCache.put(Pair(albumId, artistId), performanceId)
                    }
                    processTrack(cursor, columns, retriever, trackId, genreId, artistId, albumId, performanceId)
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
            "String Quartet" to "Classical"
        )

        val classicalId = genreRepository.findOrInsertGenreByName(GENRE_CLASSICAL)
        parentGenreIdCache[GENRE_CLASSICAL] = classicalId
    }

    private suspend fun processGenre(cursor: Cursor, columns: ColumnIndices): Pair<Int, String> {
        val genreName = cursor.getStringOrNull(columns.genreName) ?: "Unknown Genre"
        val parentGenreId = getParentGenreId(genreName)

        return Pair(genreRepository.findOrInsertGenreByName(genreName, parentGenreId), genreName)
    }

    private fun getParentGenreId(genreName: String): Int? {
        val parentGenreName = genreMappings[genreName] ?: return null
        return parentGenreIdCache[parentGenreName]
    }
    //#endregion

    //region Artists
    private suspend fun processArtist(cursor: Cursor, columns: ColumnIndices): Int {
        val artistId = cursor.getLongOrNull(columns.artistId) ?: -1
        val artistName = getArtistName(cursor, columns)
        artistRepository.insert(
            Artist(
                artistId.toInt(),
                artistName,
            )
        )
        return artistId.toInt()
    }
    //#endregion

    //#region Album Artists
    private suspend fun processAlbumArtist(
        cursor: Cursor,
        columns: ColumnIndices,
        retriever: MediaMetadataRetriever,
        genreId: Int,
    ): Int {
        var albumArtistName = cursor.getStringOrNull(columns.albumArtistName)
        if (albumArtistName == null) {
            albumArtistName =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)
                    ?: "Unknown Artist"
        }
        val albumArtistId = albumArtistRepository.findOrInsertAlbumArtist(
            albumArtistName,
            genreId
        )
        return albumArtistId
    }
    //#endregion

    //#region Albums
    private suspend fun processAlbum(
        cursor: Cursor,
        columns: ColumnIndices,
        retriever: MediaMetadataRetriever,
        trackId: Long,
        albumArtistId: Int,
    ): Int {
        val albumId = cursor.getLongOrNull(columns.albumId) ?: -1
        val albumName = getAlbumName(cursor, columns, retriever)
        val albumYear = getYear(cursor, columns, retriever)

        val albumArtworkBitmap = getAlbumArtwork(context, trackId)
        val artworkPath = albumArtworkBitmap?.let { bitmap ->
            saveAlbumArtworkToFile(context, bitmap, albumId)
        }
        albumRepository.insert(
            Album(
                albumId.toInt(),
                albumName,
                albumArtistId,
                albumYear,
                artworkPath
            )
        )
        return albumId.toInt()
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
        albumId: Long,
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
    private suspend fun processTrack(
        cursor: Cursor,
        columns: ColumnIndices,
        retriever: MediaMetadataRetriever,
        trackId: Long,
        genreId: Int,
        artistId: Int,
        albumId: Int,
        performanceId: Int?,
    ) {
        val trackTitle = cursor.getStringOrNull(columns.trackTitle) ?: "Unknown Title"
        var trackNumber = cursor.getIntOrNull(columns.trackNumber) ?: 0
        if (trackNumber == 0) {
            trackNumber =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)
                    ?.toIntOrNull() ?: -1
        }

        var discNumber = cursor.getIntOrNull(columns.discNumber) ?: 0
        if (discNumber == 0) {
            discNumber =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER)
                    ?.toIntOrNull() ?: -1
        }
        val trackDuration = cursor.getIntOrNull(columns.trackDuration) ?: 0
        trackRepository.insert(
            Track(
                trackId.toInt(),
                trackTitle,
                trackNumber,
                albumId,
                artistId,
                genreId,
                trackDuration.toDuration(DurationUnit.MILLISECONDS),
                discNumber,
                performanceId
            )
        )
    }
    //#endregion

    //#region Performance
    private suspend fun processPerformance(
        cursor: Cursor,
        columns: ColumnIndices,
        retriever: MediaMetadataRetriever,
        albumId: Int,
        artistId: Int,
    ): Int {
        val albumName = getAlbumName(cursor, columns, retriever)
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
                    retriever
                )
            )
        )
    }
    //#endregion

    private fun getYear(
        cursor: Cursor,
        columns: ColumnIndices,
        retriever: MediaMetadataRetriever
    ): String {
        var year = cursor.getStringOrNull(columns.year)
        if (year == null) {
            year =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
                    ?: "Unknown Year"
        }
        if (year != "Unknown Year") {
            year = year.take(4)
        }
        return year
    }

    private fun getAlbumName(
        cursor: Cursor,
        columns: ColumnIndices,
        retriever: MediaMetadataRetriever
    ): String {
        var albumName = cursor.getStringOrNull(columns.albumName)
        if (albumName == null) {
            albumName =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)
                    ?: "Unknown Album"
        }
        return albumName
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

    private fun getMediaMetadataRetriever(trackId: Long): MediaMetadataRetriever {
        val contentUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            trackId
        )
        val retriever = MediaMetadataRetriever()
        val afd = context.contentResolver.openAssetFileDescriptor(contentUri, "r")
        if (afd != null) {
            retriever.setDataSource(afd.fileDescriptor)
        }

        return retriever
    }
    //#endregion
}