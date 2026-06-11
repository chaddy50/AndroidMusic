package com.chaddy50.musicapp.data.api.listenBrainz

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "listenbrainz_prefs")

class ListenBrainzPreferences(private val context: Context) : IListenBrainzPreferences {

    private val TOKEN_KEY = stringPreferencesKey("listenbrainz_token")

    override val token: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[TOKEN_KEY]?.ifBlank { null }
    }

    override suspend fun getToken(): String? = token.first()

    override suspend fun setToken(token: String?) {
        context.dataStore.edit { prefs ->
            if (token.isNullOrBlank()) {
                prefs.remove(TOKEN_KEY)
            } else {
                prefs[TOKEN_KEY] = token
            }
        }
    }
}
