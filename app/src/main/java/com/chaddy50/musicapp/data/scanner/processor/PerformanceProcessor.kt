package com.chaddy50.musicapp.data.scanner.processor

import com.chaddy50.musicapp.data.entity.Performance
import com.chaddy50.musicapp.data.repository.PerformanceRepository
import com.chaddy50.musicapp.data.scanner.util.ArtworkSaver
import com.chaddy50.musicapp.data.scanner.util.CursorData

class PerformanceProcessor(
    private val performanceRepository: PerformanceRepository,
    private val artworkSaver: ArtworkSaver,
) {
    private val performanceIdCache: MutableMap<Pair<Long, Long>, Triple<Long, String?, String>> = mutableMapOf()

    suspend fun process(
        cursorData: CursorData,
        isClassical: Boolean,
        trackId: Long,
        genreId: Long,
        albumId: Long,
        artistId: Long,
        yearResolver: () -> String,
    ): Triple<Long, String?, String>? {
        if (!isClassical) return null

        val performance = performanceIdCache[Pair(albumId, artistId)]
        if (performance != null) {
            return Triple(performance.first, performance.second, performance.third)
        }

        val albumName = cursorData.albumName ?: "Unknown Album"
        val artistName = cursorData.artistName ?: "Unknown Artist"
        val year = yearResolver()

        val performanceId = performanceRepository.insert(
            Performance(
                0,
                albumId,
                albumName,
                artistId,
                artistName,
                year,
                genreId
            )
        )

        val performanceArtworkPath = artworkSaver.loadAndSaveArtwork(trackId, performanceId)

        performanceIdCache.put(Pair(albumId, artistId), Triple(performanceId, performanceArtworkPath, year))
        return Triple(performanceId, performanceArtworkPath, year)
    }
}
