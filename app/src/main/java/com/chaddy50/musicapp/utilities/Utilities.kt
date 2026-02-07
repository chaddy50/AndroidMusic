package com.chaddy50.musicapp.utilities

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
