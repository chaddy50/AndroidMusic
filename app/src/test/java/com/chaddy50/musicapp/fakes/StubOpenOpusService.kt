package com.chaddy50.musicapp.fakes

import com.chaddy50.musicapp.data.api.openOpus.ComposerSearchResponse
import com.chaddy50.musicapp.data.api.openOpus.OpenOpusService

class StubOpenOpusService : OpenOpusService {
    override suspend fun searchComposers(query: String): ComposerSearchResponse =
        ComposerSearchResponse(status = null, composers = null)
}
