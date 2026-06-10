package com.chaddy50.musicapp.fakes

import com.chaddy50.musicapp.data.api.audioDb.IAudioDbRepository

class FakeAudioDbRepository(
    private val portraitUrl: String? = null,
) : IAudioDbRepository {
    override suspend fun fetchArtistPortraitUrl(artistName: String): String? = portraitUrl
}
