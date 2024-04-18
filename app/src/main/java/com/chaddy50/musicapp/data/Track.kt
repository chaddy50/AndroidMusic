package com.chaddy50.musicapp.data

import kotlin.time.Duration

data class Track(
    val title: String,
    val number: Int,
    val albumID: Int,
    val artistID: Int,
    val genreID: Int,
    val duration: Duration,
)