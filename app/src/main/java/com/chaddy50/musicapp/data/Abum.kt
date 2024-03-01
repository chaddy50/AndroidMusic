package com.chaddy50.musicapp.data

data class Album (
    val id: Int,
    val title: String,
    val artistID: Int,
    val year: String? = "Unknown",
    val numberOfTracks: Int? = null,
    val durationInSeconds: Int? = null,
)