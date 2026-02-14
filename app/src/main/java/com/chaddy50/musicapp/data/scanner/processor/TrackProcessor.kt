package com.chaddy50.musicapp.data.scanner.processor

import android.content.ContentUris
import android.provider.MediaStore
import com.chaddy50.musicapp.data.entity.Track
import com.chaddy50.musicapp.data.scanner.util.CursorData
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class TrackProcessor() {
    fun process(
        cursorData: CursorData,
        trackId: Long,
        trackNumber: Int,
        genreId: Long,
        genreName: String,
        parentGenreId: Long?,
        parentGenreName: String?,
        artistId: Long,
        artistName: String,
        albumId: Long,
        albumName: String,
        albumArtworkPath: String?,
        albumArtistId: Long,
        albumArtistName: String,
        performanceId: Long?,
        year: String,
    ): Track {
        val trackUri = ContentUris.withAppendedId(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            trackId
        ).toString()
        val trackTitle = cursorData.trackTitle ?: "Unknown Title"
        val discNumber = cursorData.discNumber ?: 0
        val trackDuration = cursorData.trackDuration ?: 0

        return Track(
            trackId,
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
            performanceId,
            albumArtworkPath,
            year
        )
    }
}