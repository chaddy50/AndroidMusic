package com.chaddy50.froh.fakes

import com.chaddy50.froh.data.api.openOpus.ComposerSearchResponse
import com.chaddy50.froh.data.api.openOpus.OpenOpusService

class StubOpenOpusService : OpenOpusService {
    override suspend fun searchComposers(query: String): ComposerSearchResponse =
        ComposerSearchResponse(status = null, composers = null)
}
