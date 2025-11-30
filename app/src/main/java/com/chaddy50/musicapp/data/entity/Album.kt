package com.chaddy50.musicapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums")
data class Album(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val artistId: Int,
    val year: String,
    val artworkPath: String? = null,
    val numberOfTracks: Int? = null,
    val durationInSeconds: Int? = null,
)