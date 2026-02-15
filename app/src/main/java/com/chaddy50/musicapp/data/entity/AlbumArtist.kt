package com.chaddy50.musicapp.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "albumArtists",
    indices = [Index(value = ["name"], unique = true)]
)
data class AlbumArtist(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val sortName: String,
    val genreId: Long,
    val portraitPath: String? = null,
)
