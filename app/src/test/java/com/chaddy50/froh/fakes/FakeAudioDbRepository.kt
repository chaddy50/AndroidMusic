package com.chaddy50.froh.fakes

import com.chaddy50.froh.data.api.audioDb.IAudioDbRepository

class FakeAudioDbRepository(
    private val portraitUrl: String? = null,
) : IAudioDbRepository {
    override suspend fun fetchArtistPortraitUrl(artistName: String): String? = portraitUrl
}
