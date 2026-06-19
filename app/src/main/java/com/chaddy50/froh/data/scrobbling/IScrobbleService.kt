package com.chaddy50.froh.data.scrobbling

data class ScrobbleTrackMetadata(
    val artistName: String,
    val trackName: String,
    val releaseName: String? = null,
    val trackNumber: Int? = null,
    val durationMs: Long? = null,
)

interface IScrobbleService {
    suspend fun submitListen(
        track: ScrobbleTrackMetadata,
        listenedAtSeconds: Long
    ): Boolean

    suspend fun submitNowPlaying(
        track: ScrobbleTrackMetadata
    ): Boolean

    suspend fun clearNowPlaying(): Boolean
}
