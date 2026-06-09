package com.chaddy50.musicapp.fakes

import com.chaddy50.musicapp.data.entity.Track
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun testTrack(
    id: Long = 1L,
    uri: String = "content://media/audio/$id",
    title: String = "Track $id",
    number: Int = 1,
    albumId: Long = 1L,
    albumName: String = "Album",
    artistId: Long = 1L,
    artistName: String = "Artist",
    albumArtistId: Long = 1L,
    albumArtistName: String = "Artist",
    genreId: Long = 1L,
    genreName: String = "Rock",
    parentGenreId: Long? = null,
    parentGenreName: String? = null,
    durationMs: Long = 200000L,
    discNumber: Int = 1,
    performanceId: Long? = null,
    artworkPath: String? = null,
    year: String = "2024",
) = Track(
    id = id,
    uri = uri,
    title = title,
    number = number,
    albumId = albumId,
    albumName = albumName,
    artistId = artistId,
    artistName = artistName,
    albumArtistId = albumArtistId,
    albumArtistName = albumArtistName,
    genreId = genreId,
    genreName = genreName,
    parentGenreId = parentGenreId,
    parentGenreName = parentGenreName,
    duration = durationMs.toDuration(DurationUnit.MILLISECONDS),
    discNumber = discNumber,
    performanceId = performanceId,
    artworkPath = artworkPath,
    year = year,
)
