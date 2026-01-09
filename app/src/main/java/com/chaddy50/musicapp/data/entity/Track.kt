package com.chaddy50.musicapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Duration

@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val uri: String,
    val title: String,
    val number: Int,
    val albumId: Int,
    val albumName: String,
    val artistId: Int,
    val artistName: String,
    val albumArtistId: Int,
    val albumArtistName: String,
    val genreId: Int,
    val genreName: String,
    val parentGenreId: Int?,
    val parentGenreName: String?,
    val duration: Duration,
    val discNumber: Int,
    val performanceId: Int? = null,
)