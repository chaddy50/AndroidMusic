package com.chaddy50.musicapp.data.api.listenBrainz

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeListenBrainzPreferences(
    initialToken: String? = null,
) : IListenBrainzPreferences {

    private val _token = MutableStateFlow(initialToken)

    override val token: Flow<String?> = _token

    override suspend fun getToken(): String? = _token.value

    override suspend fun setToken(token: String?) {
        _token.value = token?.ifBlank { null }
    }
}
