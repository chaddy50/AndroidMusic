package com.chaddy50.musicapp.data.scanner.processor

import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.scanner.util.CursorData

class AlbumArtistProcessor(
    private val albumArtistRepository: AlbumArtistRepository,
) {
    private val processedAlbumArtists: MutableMap<String, Pair<Long, String>> = mutableMapOf()

    suspend fun process(
        cursorData: CursorData,
        genreId: Long,
    ): Pair<Long, String> {
        val albumArtistName = cursorData.albumArtistName ?: "Unknown Artist"
        processedAlbumArtists[albumArtistName]?.let { return it }

        val albumArtistId = albumArtistRepository.findOrInsertAlbumArtist(
            albumArtistName,
            genreId,
        )
        val newAlbumArtist = Pair(albumArtistId, albumArtistName)
        processedAlbumArtists[albumArtistName] = newAlbumArtist
        return newAlbumArtist
    }
}