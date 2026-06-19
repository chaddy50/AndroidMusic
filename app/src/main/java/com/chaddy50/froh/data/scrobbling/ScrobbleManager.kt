package com.chaddy50.froh.data.scrobbling

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "ScrobbleManager"
private const val FOUR_MINUTES_MS = 4 * 60 * 1000L
internal const val NOW_PLAYING_CLEAR_DELAY_MS = 30_000L

class ScrobbleManager(
    private val services: List<IScrobbleService>,
) {
    var scope: CoroutineScope? = null

    private var currentTrack: ScrobbleTrackMetadata? = null
    private var currentDurationMs: Long = 0
    private var hasScrobbled: Boolean = false
    private var trackStartedAtSeconds: Long = 0
    private var clearNowPlayingJob: Job? = null

    fun onPlaybackStarted(
        artistName: String?,
        trackName: String?,
        releaseName: String?,
        trackNumber: Int?,
        durationMs: Long
    ) {
        if (artistName == null || trackName == null) return

        clearNowPlayingJob?.cancel()
        clearNowPlayingJob = null

        val newTrack = ScrobbleTrackMetadata(
            artistName = artistName,
            trackName = trackName,
            releaseName = releaseName,
            trackNumber = trackNumber,
            durationMs = if (durationMs > 0) durationMs else null,
        )

        if (newTrack != currentTrack) {
            currentTrack = newTrack
            currentDurationMs = durationMs
            hasScrobbled = false
            trackStartedAtSeconds = System.currentTimeMillis() / 1000
            Log.d(TAG, "Track started: $artistName - $trackName (${durationMs}ms)")
        }

        scope?.launch {
            services.forEach { service ->
                service.submitNowPlaying(newTrack)
            }
        }
    }

    fun onPlaybackStopped() {
        clearNowPlayingJob?.cancel()
        clearNowPlayingJob = scope?.launch {
            delay(NOW_PLAYING_CLEAR_DELAY_MS)
            services.forEach { service ->
                service.clearNowPlaying()
            }
        }
    }

    fun onPlaybackPositionUpdated(positionMs: Long) {
        if (hasScrobbled) return
        if (currentDurationMs <= 0) return

        val track = currentTrack ?: return

        val threshold = minOf(currentDurationMs / 2, FOUR_MINUTES_MS)
        if (positionMs >= threshold) {
            hasScrobbled = true
            Log.d(TAG, "Scrobble threshold met at ${positionMs}ms (threshold: ${threshold}ms)")
            scope?.launch {
                services.forEach { service ->
                    service.submitListen(track, trackStartedAtSeconds)
                }
            }
        }
    }
}
