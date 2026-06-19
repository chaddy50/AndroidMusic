package com.chaddy50.froh.data.api.listenBrainz

import android.util.Log
import com.chaddy50.froh.data.scrobbling.IScrobbleService
import com.chaddy50.froh.data.scrobbling.ScrobbleTrackMetadata
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val TAG = "ListenBrainzRepository"

sealed interface TokenValidationState {
    data object Idle : TokenValidationState
    data object Validating : TokenValidationState
    data class Valid(val userName: String) : TokenValidationState
    data object Invalid : TokenValidationState
    data object NetworkError : TokenValidationState
}

class ListenBrainzRepository(
    private val service: ListenBrainzService,
    private val preferences: IListenBrainzPreferences,
    private val scope: CoroutineScope,
) : IScrobbleService {
    private val _tokenValidationState = MutableStateFlow<TokenValidationState>(TokenValidationState.Idle)
    val tokenValidationState: StateFlow<TokenValidationState> = _tokenValidationState.asStateFlow()

    init {
        scope.launch {
            val savedToken = preferences.getToken()
            if (savedToken != null) {
                _tokenValidationState.value = TokenValidationState.Validating
                val response = validateToken(savedToken)
                _tokenValidationState.value = when {
                    response == null -> TokenValidationState.NetworkError
                    response.valid -> TokenValidationState.Valid(response.user_name ?: "")
                    else -> TokenValidationState.Invalid
                }
            }
        }
    }

    suspend fun validateToken(token: String): ValidateTokenResponse? {
        return try {
            service.validateToken("Token $token")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to validate token", e)
            null
        }
    }

    fun saveToken(token: String) {
        if (token.isBlank()) return

        _tokenValidationState.value = TokenValidationState.Validating

        scope.launch {
            val response = validateToken(token)
            if (response == null) {
                _tokenValidationState.value = TokenValidationState.NetworkError
            } else if (response.valid) {
                preferences.setToken(token)
                _tokenValidationState.value = TokenValidationState.Valid(response.user_name ?: "")
            } else {
                _tokenValidationState.value = TokenValidationState.Invalid
            }
        }
    }

    fun logout() {
        scope.launch {
            preferences.setToken(null)
            _tokenValidationState.value = TokenValidationState.Idle
        }
    }

    override suspend fun submitListen(
        track: ScrobbleTrackMetadata,
        listenedAtSeconds: Long
    ): Boolean {
        val token = preferences.getToken() ?: return false

        val request = ListenBrainzSubmitRequest(
            listen_type = "single",
            payload = listOf(
                ListenPayload(
                    listened_at = listenedAtSeconds,
                    track_metadata = track.toListenBrainzMetadata()
                )
            )
        )

        return try {
            service.submitListens("Token $token", request)
            Log.d(TAG, "Scrobbled: ${track.artistName} - ${track.trackName}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to submit listen", e)
            false
        }
    }

    override suspend fun submitNowPlaying(
        track: ScrobbleTrackMetadata
    ): Boolean {
        val token = preferences.getToken() ?: return false

        val request = ListenBrainzSubmitRequest(
            listen_type = "playing_now",
            payload = listOf(
                ListenPayload(
                    track_metadata = track.toListenBrainzMetadata()
                )
            )
        )

        return try {
            service.submitListens("Token $token", request)
            Log.d(TAG, "Now playing: ${track.artistName} - ${track.trackName}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to submit now playing", e)
            false
        }
    }

    override suspend fun clearNowPlaying(): Boolean {
        val token = preferences.getToken() ?: return false

        return try {
            service.deletePlayingNow("Token $token")
            Log.d(TAG, "Cleared now playing")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to clear now playing", e)
            false
        }
    }

    private fun ScrobbleTrackMetadata.toListenBrainzMetadata(): TrackMetadata {
        val additionalInfo = AdditionalInfo(
            tracknumber = trackNumber?.toString(),
            duration_ms = durationMs,
            submission_client = SUBMISSION_CLIENT,
            submission_client_version = SUBMISSION_CLIENT_VERSION,
        )
        return TrackMetadata(
            artist_name = artistName,
            track_name = trackName,
            release_name = releaseName,
            additional_info = additionalInfo,
        )
    }

    companion object {
        private const val SUBMISSION_CLIENT = "MusicApp"
        private const val SUBMISSION_CLIENT_VERSION = "1.0"
    }
}