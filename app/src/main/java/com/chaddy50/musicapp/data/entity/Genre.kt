package com.chaddy50.musicapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "genres")
data class Genre(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
)
