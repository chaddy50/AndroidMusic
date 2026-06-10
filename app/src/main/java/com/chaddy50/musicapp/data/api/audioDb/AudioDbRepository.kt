package com.chaddy50.musicapp.data.api.audioDb

import com.chaddy50.musicapp.data.util.IArtworkDownloader
import kotlinx.coroutines.delay

private const val API_RATE_LIMIT_DELAY = 500L

interface IAudioDbRepository {
    suspend fun fetchArtistPortraitUrl(artistName: String): String?
}

class AudioDbRepository(
    private val service: AudioDbService,
    private val artworkDownloader: IArtworkDownloader
) : IAudioDbRepository {
    override suspend fun fetchArtistPortraitUrl(artistName: String): String? {
        return try {
            val response = service.searchArtist(artistName)
            delay(API_RATE_LIMIT_DELAY)
            val thumbnailUrl = response.artists?.firstOrNull()?.thumbnailUrl
            artworkDownloader.downloadArtwork(thumbnailUrl, "artist_portraits", artistName.hashCode().toLong())
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
