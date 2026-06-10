package com.chaddy50.musicapp.fakes

import com.chaddy50.musicapp.data.api.openOpus.IOpenOpusRepository
import com.chaddy50.musicapp.data.api.openOpus.OpenOpusComposer

class FakeOpenOpusRepository(
    private val composer: OpenOpusComposer? = null,
) : IOpenOpusRepository {
    override suspend fun findComposerByName(name: String): OpenOpusComposer? = composer
}
