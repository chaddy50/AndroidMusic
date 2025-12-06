package com.chaddy50.musicapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "performances",
    foreignKeys = [
        ForeignKey(
            entity = Album::class,
            parentColumns = ["id"],
            childColumns = ["albumId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Artist::class,
            parentColumns = ["id"],
            childColumns = ["artistId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["albumId", "artistId", "year"], unique = true),
        Index(value = ["albumId"]),
    ]
)
data class Performance(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val albumId: Int,
    val albumName: String,
    val artistId: Int,
    val artistName: String,
    val year: String
)
