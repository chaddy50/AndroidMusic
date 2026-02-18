package com.chaddy50.musicapp.data.scanner.processor

import com.chaddy50.musicapp.data.entity.Artist
import com.chaddy50.musicapp.data.repository.IArtistRepository
import com.chaddy50.musicapp.data.scanner.util.CursorData

class ArtistProcessor(
    private val artistRepository: IArtistRepository,
) {
    private val processedArtists = mutableMapOf<Long, String>()

    suspend fun process(
        cursorData: CursorData
    ): Pair<Long, String> {
        val artistId = cursorData.artistId ?: -1
        processedArtists[artistId]?.let { return Pair(artistId, it) }

        val artistName = cursorData.artistName ?: "Unknown Artist"
        artistRepository.insert(
            Artist(
                artistId,
                artistName,
            )
        )
        processedArtists[artistId] = artistName
        return Pair(artistId, artistName)
    }
}