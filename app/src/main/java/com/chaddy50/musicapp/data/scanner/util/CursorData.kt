package com.chaddy50.musicapp.data.scanner.util

import android.database.Cursor
import android.provider.MediaStore
import androidx.core.database.getIntOrNull
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull

data class CursorData(
    val trackId: Long,
    val trackTitle: String?,
    val trackNumber: Int?,
    val trackDuration: Long?,
    val discNumber: Int?,
    val genreName: String?,
    val artistId: Long?,
    val artistName: String?,
    val albumArtistName: String?,
    val albumId: Long?,
    val albumName: String?,
    val year: String?
)

data class ColumnIndices(
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

fun getDataFromCursor(cursor: Cursor, columns: ColumnIndices): CursorData {
    return CursorData(
        cursor.getLong(columns.trackId),
        cursor.getStringOrNull(columns.trackTitle),
        cursor.getIntOrNull(columns.trackNumber),
        cursor.getLongOrNull(columns.trackDuration),
        cursor.getIntOrNull(columns.discNumber),
        cursor.getStringOrNull(columns.genreName),
        cursor.getLongOrNull(columns.artistId),
        cursor.getStringOrNull(columns.artistName),
        cursor.getStringOrNull(columns.albumArtistName),
        cursor.getLongOrNull(columns.albumId),
        cursor.getStringOrNull(columns.albumName),
        cursor.getStringOrNull(columns.year)
    )
}
