package com.chaddy50.musicapp.utilities

import java.util.Locale


fun stripArticles(name: String): String {
    val prefixes = listOf("The ", "A ", "An ")
    for (prefix in prefixes) {
        if (name.startsWith(prefix, ignoreCase = true)) {
            return name.substring(prefix.length).trim()
        }
    }
    return name.trim()
}

fun formatMillisecondsIntoMinutesAndSeconds(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}