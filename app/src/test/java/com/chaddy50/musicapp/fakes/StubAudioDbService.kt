package com.chaddy50.musicapp.fakes

import com.chaddy50.musicapp.data.api.audioDb.AudioDbArtistSearchResponse
import com.chaddy50.musicapp.data.api.audioDb.AudioDbService

class StubAudioDbService(
    private val response: AudioDbArtistSearchResponse = AudioDbArtistSearchResponse(artists = null),
    private val exception: Exception? = null,
) : AudioDbService {
    override suspend fun searchArtist(name: String): AudioDbArtistSearchResponse {
        exception?.let { throw it }
        return response
    }
}
