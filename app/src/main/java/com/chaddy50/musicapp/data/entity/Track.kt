package com.chaddy50.musicapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Duration

@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val uri: String,
    val title: String,
    val number: Int,
    val albumId: Long,
    val albumName: String,
    val artistId: Long,
    val artistName: String,
    val albumArtistId: Long,
    val albumArtistName: String,
    val genreId: Long,
    val genreName: String,
    val parentGenreId: Long?,
    val parentGenreName: String?,
    val duration: Duration,
    val discNumber: Int,
    val performanceId: Long? = null,
    val artworkPath: String? = null,
    val year: String
)