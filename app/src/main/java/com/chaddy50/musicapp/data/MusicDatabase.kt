package com.chaddy50.musicapp.data

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import androidx.core.database.getStringOrNull

data class MusicDatabase(
    var genres: Set<Genre>,
    var artists: Set<Artist>,
    var albums: Set<Album>,
    var tracks: Set<Track>,
) {

    fun initialize(context: Context) {
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns.GENRE_ID,
            MediaStore.Audio.AudioColumns.GENRE,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ARTIST_ID,
            MediaStore.Audio.AudioColumns.ALBUM_ARTIST,
            MediaStore.Audio.AudioColumns.YEAR,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.TRACK,
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
            while (cursor.moveToNext()) {
                val indexer = MusicIndexer(cursor)

                val genre = indexer.getGenre()
                genres = genres.plus(genre)

                val album = indexer.getAlbum()
                albums = albums.plus(album)

                val artist = indexer.getArtist()
                artists = artists.plus(artist)

                val track = indexer.getTrack()
                tracks = tracks.plus(track)
            }
        }
    }
}

class MusicIndexer(
    private val cursor: Cursor
) {

    //region Column Indices
    private var columnIndexGenreID = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.GENRE_ID)
    private var columnIndexGenreTitle = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.GENRE)
    private var columnIndexAlbumID = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID)
    private var columnIndexAlbumTitle = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)
    private var columnIndexArtistID = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST_ID)
    private var columnIndexAlbumArtist =  cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ARTIST)
    private var columnIndexYear = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.YEAR)
    private var columnIndexTrackTitle = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)
    private var columnIndexTrackNumber = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TRACK)
    private var columnIndexTrackDuration = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)
    //endregion

    fun getAlbum(): Album {
        return Album(
            cursor.getInt(columnIndexAlbumID),
            cursor.getString(columnIndexAlbumTitle) ?: "Unknown Album",
            cursor.getInt(columnIndexArtistID),
            cursor.getStringOrNull(columnIndexYear)
        )
    }

    fun getGenre(): Genre {
        return Genre(
            cursor.getInt(columnIndexGenreID),
            cursor.getStringOrNull(columnIndexGenreTitle) ?: "Unknown Genre"
        )
    }

    fun getArtist(): Artist {
        return Artist(
            cursor.getInt(columnIndexArtistID),
            cursor.getString(columnIndexAlbumArtist) ?: "Unknown Artist",
            cursor.getInt(columnIndexGenreID)
        )
    }

    fun getTrack(): Track {
        return Track(
            cursor.getString(columnIndexTrackTitle),
            cursor.getInt(columnIndexTrackNumber),
            cursor.getInt(columnIndexAlbumID),
            cursor.getInt(columnIndexArtistID),
            cursor.getInt(columnIndexGenreID),
            cursor.getInt(columnIndexTrackDuration),
        )
    }
}