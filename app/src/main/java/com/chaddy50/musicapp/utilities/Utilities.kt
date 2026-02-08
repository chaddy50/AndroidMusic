package com.chaddy50.musicapp.utilities

import java.util.Locale

private val cataloguePattern = Regex("""(?i)(?:Op\.?|K\.?|BWV|Hob\.?|RV|D\.?|S\.?|M\.?|L\.?)\s*(\d+)""")

fun stripArticles(name: String): String {
    val prefixes = listOf("The ", "A ", "An ")
    for (prefix in prefixes) {
        if (name.startsWith(prefix, ignoreCase = true)) {
            return name.substring(prefix.length).trim()
        }
    }
    return name.trim()
}

fun extractCatalogNumber(albumName: String): Int {
    val match = cataloguePattern.find(albumName)

    // Group 1 contains just the digits (\d+)
    return match?.groupValues?.get(1)?.toIntOrNull() ?: 99999
}

fun formatMillisecondsIntoMinutesAndSeconds(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}