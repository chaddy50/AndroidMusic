package com.chaddy50.musicapp.data

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.provider.MediaStore
import android.util.Size
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.data.entity.Artist
import com.chaddy50.musicapp.data.entity.Genre
import com.chaddy50.musicapp.data.entity.Track
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.AlbumRepository
import com.chaddy50.musicapp.data.repository.ArtistRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class MusicScanner(
    val context: Context,
    val genreRepository: GenreRepository,
    var artistRepository: ArtistRepository,
    var albumArtistRepository: AlbumArtistRepository,
    var albumRepository: AlbumRepository,
    var trackRepository: TrackRepository,
) {
    suspend fun scan() {
        withContext(Dispatchers.IO) {
            val projection = arrayOf(
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.GENRE_ID,
                MediaStore.Audio.AudioColumns.GENRE,
                MediaStore.Audio.AudioColumns.ALBUM_ID,
                MediaStore.Audio.AudioColumns.ALBUM,
                MediaStore.Audio.AudioColumns.ARTIST_ID,
                MediaStore.Audio.AudioColumns.ARTIST,
                MediaStore.Audio.AudioColumns.ALBUM_ARTIST,
                MediaStore.Audio.AudioColumns.YEAR,
                MediaStore.Audio.AudioColumns.TITLE,
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
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)
                val artistIdColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST_ID)
                val artistNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
                val albumIdColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ID)
                val albumNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM)
                val albumArtistNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ALBUM_ARTIST)
                val trackNumberColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER)
                val trackDurationColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
                val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.YEAR)
                val genreIdColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.GENRE_ID)
                val genreNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.GENRE)

                while (cursor.moveToNext()) {
                    val trackId = cursor.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        trackId
                    )
                    val retriever = MediaMetadataRetriever()
                    val afd = context.contentResolver.openAssetFileDescriptor(contentUri, "r")
                    if (afd != null) {
                        retriever.setDataSource(afd.fileDescriptor)
                    }

                    val genreId = cursor.getLongOrNull(genreIdColumn) ?: -1
                    val genreName = cursor.getStringOrNull(genreNameColumn) ?: "Unknown Genre"
                    genreRepository.insert(
                        Genre(
                            genreId.toInt(),
                            genreName,
                        )
                    )

                    val artistId = cursor.getLongOrNull(artistIdColumn) ?: -1
                    val artistName = cursor.getStringOrNull(artistNameColumn) ?: "Unknown Artist"
                    artistRepository.insert(
                        Artist(
                            artistId.toInt(),
                            artistName,
                        )
                    )

                    var albumArtistName = cursor.getStringOrNull(albumArtistNameColumn)
                    if (albumArtistName == null) {
                        albumArtistName =
                            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)
                                ?: "Unknown Artist"
                    }
                    val albumArtistId = albumArtistRepository.findOrInsertAlbumArtist(
                        albumArtistName,
                        genreId.toInt()
                    )

                    val albumId = cursor.getLongOrNull(albumIdColumn) ?: -1
                    var albumName = cursor.getStringOrNull(albumNameColumn)
                    if (albumName == null) {
                        albumName =
                            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
                                ?: "Unknown Album"
                    }
                    var albumYear = cursor.getStringOrNull(yearColumn) // Ensure year is 4 digits
                    if (albumYear == null) {
                        albumYear =
                            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
                                ?: "Unknown Year"
                    }
                    if (albumYear != "Unknown Year") {
                        albumYear = albumYear.take(4)
                    }

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

                    val trackTitle = cursor.getStringOrNull(titleColumn) ?: "Unknown Title"
                    var trackNumber = cursor.getIntOrNull(trackNumberColumn) ?: 0
                    if (trackNumber == 0) {
                        trackNumber =
                            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)
                                ?.toIntOrNull() ?: -1
                    }
                    val trackDuration = cursor.getIntOrNull(trackDurationColumn) ?: 0
                    trackRepository.insert(
                        Track(
                            trackId.toInt(),
                            trackTitle,
                            trackNumber,
                            albumId.toInt(),
                            artistId.toInt(),
                            genreId.toInt(),
                            trackDuration.toDuration(DurationUnit.MILLISECONDS),
                        )
                    )
                }
            }
        }
    }
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