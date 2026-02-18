package com.chaddy50.musicapp.data.api.openOpus

import com.chaddy50.musicapp.utilities.stripDiacritics

class OpenOpusRepository(private val service: OpenOpusService) {
    suspend fun findComposerByName(name: String): OpenOpusComposer? {
        // Try the full name first
        val response = service.searchComposers(name)
        response.composers?.firstOrNull()?.let { return it }

        // Strip diacritics and retry (e.g. "Saint-Saëns" → "Saint-Saens")
        val normalized = stripDiacritics(name)
        if (normalized != name) {
            val normalizedResponse = service.searchComposers(normalized)
            normalizedResponse.composers?.firstOrNull()?.let { return it }
        }

        // Try individual words of the name (e.g. "Saint-Saëns" → "Saint", "Saens")
        val words = normalized.split(" ", "-").filter { it.isNotBlank() }
        for (word in words) {
            val wordResponse = service.searchComposers(word)
            wordResponse.composers?.firstOrNull()?.let { return it }
        }

        return null
    }

}
