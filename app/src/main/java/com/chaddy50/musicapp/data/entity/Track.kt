package com.chaddy50.musicapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.time.Duration

@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val number: Int,
    val albumId: Int,
    val artistId: Int,
    val genreId: Int,
    val duration: Duration,
    val discNumber: Int,
    val performanceId: Int? = null,
)