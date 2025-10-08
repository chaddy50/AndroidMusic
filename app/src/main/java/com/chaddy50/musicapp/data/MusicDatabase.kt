package com.chaddy50.musicapp.data

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import java.io.IOException
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

data class MusicDatabase(
    var genres: Set<Genre>,
    var artists: Set<Artist>,
    var albumArtists: Set<AlbumArtist>,
    var albums: Set<Album>,
    var tracks: Set<Track>,
) {
    fun initialize(context: Context) {
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.GENRE_ID,
            MediaStore.Audio.AudioColumns.GENRE,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ARTIST_ID,
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
            MediaStore.Audio.Media.DEFAULT_SORT_ORDER,
            null
        )?.use { cursor ->
            val numberOfFilesTotal = cursor.count
            var numberOfFilesProcessed = 0
            while (cursor.moveToNext()) {
                numberOfFilesProcessed++

                val trackID = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID) ?: -1)
                val contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, trackID)
                val retriever = MediaMetadataRetriever()
                val afd = context.contentResolver.openAssetFileDescriptor(contentUri, "r")
                if (afd != null) {
                    retriever.setDataSource(afd.fileDescriptor)
                }

                val indexer = MusicIndexer(cursor, retriever, this)

                val genre = indexer.getGenre()
                genres = genres.plus(genre)

                val album = indexer.getAlbum()
                if (album != null) {
                    albums = albums.plus(album)
                }

                val albumArtist = indexer.getAlbumArtist()
                albumArtists = albumArtists.plus(albumArtist)

                val track = indexer.getTrack(context)
                tracks = tracks.plus(track)
            }
        }
    }
}

class MusicIndexer(
    private val cursor: Cursor,
    private val retriever: MediaMetadataRetriever,
    private val musicDatabase: MusicDatabase,
) {

    //region Column Indices
    private var columnIndexID = cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID)
    private var columnIndexGenreID = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.GENRE_ID)
    private var columnIndexGenreTitle = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.GENRE)
    private var columnIndexAlbumID = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID)
    private var columnIndexAlbumTitle = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)
    private var columnIndexArtistID = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST_ID)
    private var columnIndexAlbumArtist =
        cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ARTIST)
    private var columnIndexYear = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.YEAR)
    private var columnIndexTrackTitle = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)
    private var columnIndexTrackNumber =
        cursor.getColumnIndex(MediaStore.Audio.AudioColumns.CD_TRACK_NUMBER)
    private var columnIndexTrackDuration =
        cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)
    //endregion

    fun getGenre(): Genre {
        return Genre(
            cursor.getIntOrNull(columnIndexGenreID) ?: -1,
            cursor.getStringOrNull(columnIndexGenreTitle) ?: "Unknown Genre"
        )
    }

    fun getAlbum(): Album? {
        val albumID = cursor.getIntOrNull(columnIndexAlbumID) ?: -1

        if (musicDatabase.albums.find { it.id == albumID } != null) {
            return null
        }

        var title = cursor.getStringOrNull(columnIndexAlbumTitle)
        if (title == null) {
            title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)
        }

        var artist = cursor.getStringOrNull(columnIndexAlbumArtist)
        if (artist == null) {
            artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)
        }

        var year = cursor.getStringOrNull(columnIndexYear)
        if (year == null) {
            year = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)
        }

        return Album(
            albumID,
            title ?: "Unknown Title",
            artist ?: "Unknown Artist",
            year ?: "Unknown Year"
        )
    }

    private fun getAlbumArtwork(trackID: Long, context: Context): Bitmap? {
        val uri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            trackID
        )

        var thumbnail: Bitmap? = null
        try {
            thumbnail = context.contentResolver.loadThumbnail(
                uri,
                Size(10000, 10000),
                null
            )
        } catch (e: IOException) {

        }
        return thumbnail
    }

    fun getAlbumArtist(): AlbumArtist {
        return AlbumArtist(
            cursor.getStringOrNull(columnIndexAlbumArtist) ?: "Unknown Artist",
            cursor.getIntOrNull(columnIndexGenreID) ?: -1,
        )
    }

    fun getTrack(context: Context): Track {
        val trackID = cursor.getLong(columnIndexID)
        val albumID = cursor.getInt(columnIndexAlbumID)

        val albumArtwork = getAlbumArtwork(trackID, context)
        val album = musicDatabase.albums.find { it.id == albumID }
        if (album != null && album.artwork == null && albumArtwork != null) {
            album.artwork = albumArtwork
        }

        var trackNumber = cursor.getIntOrNull(columnIndexTrackNumber)
        if (trackNumber == 0) {
            trackNumber = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)?.toIntOrNull()
        }

        return Track(
            cursor.getString(columnIndexTrackTitle),
            trackNumber ?: -1,
            albumID,
            cursor.getInt(columnIndexArtistID),
            cursor.getInt(columnIndexGenreID),
            cursor.getInt(columnIndexTrackDuration).toDuration(DurationUnit.MILLISECONDS)
        )
    }
}