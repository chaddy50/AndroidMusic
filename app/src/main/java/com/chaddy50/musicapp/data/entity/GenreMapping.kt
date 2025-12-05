package com.chaddy50.musicapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "genre_mappings")
data class GenreMapping(
    @PrimaryKey
    val subGenreName: String,
    val parentGenreName: String,
)