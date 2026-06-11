package com.chaddy50.musicapp.data.api.listenBrainz

import kotlinx.coroutines.flow.Flow

interface IListenBrainzPreferences {
    val token: Flow<String?>

    suspend fun getToken(): String?
    suspend fun setToken(token: String?)
}
