package com.chaddy50.musicapp.data.scanner.processor

import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.data.repository.IAlbumRepository
import com.chaddy50.musicapp.data.scanner.util.IArtworkSaver
import com.chaddy50.musicapp.data.scanner.util.CursorData

class AlbumProcessor(
    private val albumRepository: IAlbumRepository,
    private val artworkSaver: IArtworkSaver,
) {
    private val processedAlbums = mutableMapOf<Long, AlbumProcessorResult>()

    suspend fun process(
        cursorData: CursorData,
        trackId: Long,
        albumArtistId: Long,
        yearResolver: () -> String,
    ) : AlbumProcessorResult {
        val albumId = cursorData.albumId ?: -1

        processedAlbums[albumId]?.let { return it }

        val albumName = cursorData.albumName ?: "Unknown Album"
        val catalogueNumber = extractCatalogNumber(albumName)
        val albumYear = yearResolver()

        val albumArtworkPath = artworkSaver.loadAndSaveArtwork(trackId, albumId)
        albumRepository.insert(
            Album(
                albumId,
                albumName,
                catalogueNumber,
                albumArtistId,
                albumYear,
                albumArtworkPath
            )
        )
        val result = AlbumProcessorResult(albumId, albumName, albumArtworkPath, albumYear)
        processedAlbums[albumId] = result
        return result
    }
}

private val cataloguePattern = Regex("""(?i)(?:Op\.?|K\.?|BWV|Hob\.?|RV|D\.?|S\.?|M\.?|L\.?)\s*(\d+)""")
internal fun extractCatalogNumber(albumName: String): Int {
    val match = cataloguePattern.find(albumName)

    // Group 1 contains just the digits (\d+)
    return match?.groupValues?.get(1)?.toIntOrNull() ?: 99999
}

data class AlbumProcessorResult(
    val albumId: Long,
    val albumName: String,
    val artworkPath: String?,
    val year: String
)
