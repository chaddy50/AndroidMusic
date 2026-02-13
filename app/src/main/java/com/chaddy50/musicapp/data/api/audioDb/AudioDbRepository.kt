package com.chaddy50.musicapp.data.api.audioDb

import com.chaddy50.musicapp.data.util.ArtworkDownloader
import kotlinx.coroutines.delay

private const val API_RATE_LIMIT_DELAY = 500L

class AudioDbRepository(
    private val service: AudioDbService,
    private val artworkDownloader: ArtworkDownloader
) {
    suspend fun fetchArtistPortraitUrl(artistName: String): String? {
        return try {
            val response = service.searchArtist(artistName)
            delay(API_RATE_LIMIT_DELAY)
            val thumbnailUrl = response.artists?.firstOrNull()?.thumbnailUrl
            artworkDownloader.downloadArtwork(thumbnailUrl, "artist_portraits", artistName.hashCode())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
