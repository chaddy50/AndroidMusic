package com.chaddy50.froh.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums")
data class Album(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val catalogueSortIndex: Int?,
    val catalogueString: String? = null,
    val artistId: Long,
    val year: String,
    val artworkPath: String? = null,
    val numberOfTracks: Int? = null,
    val durationInSeconds: Int? = null,
)