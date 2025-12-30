package com.chaddy50.musicapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albumArtists")
data class AlbumArtist(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val sortName: String,
    val genreId: Int
)
