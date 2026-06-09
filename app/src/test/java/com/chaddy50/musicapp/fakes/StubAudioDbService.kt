package com.chaddy50.musicapp.fakes

import com.chaddy50.musicapp.data.api.audioDb.AudioDbArtistSearchResponse
import com.chaddy50.musicapp.data.api.audioDb.AudioDbService

class StubAudioDbService : AudioDbService {
    override suspend fun searchArtist(name: String): AudioDbArtistSearchResponse =
        AudioDbArtistSearchResponse(artists = null)
}
