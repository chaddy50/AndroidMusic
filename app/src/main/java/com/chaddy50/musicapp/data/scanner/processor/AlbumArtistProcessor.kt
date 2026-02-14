package com.chaddy50.musicapp.data.scanner.processor

import com.chaddy50.musicapp.data.api.audioDb.AudioDbRepository
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.scanner.util.CursorData

class AlbumArtistProcessor(
    private val albumArtistRepository: AlbumArtistRepository,
    private val audioDbRepository: AudioDbRepository,
) {
    private val processedAlbumArtists: MutableMap<String, Pair<Long, String>> = mutableMapOf()

    suspend fun process(
        cursorData: CursorData,
        genreId: Long,
        isClassical: Boolean,
        shouldFetchArtistArtwork: Boolean,
    ): Pair<Long, String> {
        val albumArtistName = cursorData.albumArtistName ?: "Unknown Artist"
        processedAlbumArtists[albumArtistName]?.let { return it }

        val portraitPath = if (!isClassical && shouldFetchArtistArtwork) audioDbRepository.fetchArtistPortraitUrl(albumArtistName) else null
        val albumArtistId = albumArtistRepository.findOrInsertAlbumArtist(
            albumArtistName,
            genreId,
            portraitPath,
        )
        val newAlbumArtist = Pair(albumArtistId, albumArtistName)
        processedAlbumArtists[albumArtistName] = newAlbumArtist
        return newAlbumArtist
    }
}