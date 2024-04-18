package com.chaddy50.musicapp.data

import android.graphics.Bitmap

data class Album(
    val id: Int,
    val title: String,
    val artist: String,
    val year: String,
    var artwork: Bitmap? = null,
    val numberOfTracks: Int? = null,
    val durationInSeconds: Int? = null,
)