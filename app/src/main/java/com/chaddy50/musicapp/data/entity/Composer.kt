package com.chaddy50.musicapp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "composers",
    foreignKeys = [
        ForeignKey(
            entity = AlbumArtist::class,
            parentColumns = ["id"],
            childColumns = ["albumArtistId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("albumArtistId", unique = true)]
)
data class Composer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val albumArtistId: Long,
    val openOpusId: Long,
    val completeName: String,
    val birthYear: String?,
    val deathYear: String?,
    val epoch: String?,
    val portraitPath: String?,
)
