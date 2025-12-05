package com.chaddy50.musicapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "genres",
    foreignKeys = [
        ForeignKey(
            Genre::class,
            ["id"],
            ["parentGenreId"],
            ForeignKey.CASCADE
        )
    ],
    indices = [Index("parentGenreId")]
)
data class Genre(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val parentGenreId: Int? = null
)
