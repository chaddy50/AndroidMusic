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
    indices = [Index("parentGenreId"), Index(value = ["name"], unique = true)]
)
data class Genre(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val parentGenreId: Long? = null
)
