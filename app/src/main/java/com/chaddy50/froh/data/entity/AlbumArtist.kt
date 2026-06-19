package com.chaddy50.froh.data.entity

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
    val portraitPath: String? = null,
)
