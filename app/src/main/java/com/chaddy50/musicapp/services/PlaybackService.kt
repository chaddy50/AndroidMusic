package com.chaddy50.musicapp.services

import android.app.PendingIntent
import android.os.Bundle
import android.util.Log
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.CommandButton
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import com.chaddy50.musicapp.MusicApplication
import com.chaddy50.musicapp.data.scrobbling.ScrobbleManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "PlaybackService"

@AndroidEntryPoint
class PlaybackService : MediaLibraryService() {
    private var mediaLibrarySession: MediaLibrarySession? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var scrobblePollingJob: Job? = null

    @Inject lateinit var scrobbleManager: ScrobbleManager

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")

        scrobbleManager.scope = serviceScope

        try {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build()

            val player = ExoPlayer.Builder(this)
                .setAudioAttributes(audioAttributes, true)
                .setHandleAudioBecomingNoisy(true)
                .build()

            val sessionActivityPendingIntent =
                packageManager?.getLaunchIntentForPackage(packageName)?.let { intent ->
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
                }

            if (sessionActivityPendingIntent == null) {
                Log.w(TAG, "sessionActivityPendingIntent is null — session will have no activity")
            }

            val callback = AutoLibraryCallback(application as MusicApplication, serviceScope)

            val sessionBuilder = MediaLibrarySession.Builder(this, player, callback)
            sessionActivityPendingIntent?.let { sessionBuilder.setSessionActivity(it) }
            mediaLibrarySession = sessionBuilder.build()

            mediaLibrarySession?.setCustomLayout(listOf(buildShuffleButton(player.shuffleModeEnabled)))

            player.addListener(object : Player.Listener {
                override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
                    mediaLibrarySession?.setCustomLayout(listOf(buildShuffleButton(shuffleModeEnabled)))
                }
            })

            player.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        notifyScrobbleTrackStarted(player)
                        startScrobblePositionPolling(player)
                    } else {
                        stopScrobblePositionPolling()
                        scrobbleManager.onPlaybackStopped()
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    if (player.isPlaying) {
                        notifyScrobbleTrackStarted(player)
                    }
                }
            })

            Log.d(TAG, "MediaLibrarySession created: $mediaLibrarySession")
        } catch (e: Exception) {
            Log.e(TAG, "onCreate failed", e)
        }
    }

    private fun notifyScrobbleTrackStarted(player: Player) {
        val metadata = player.currentMediaItem?.mediaMetadata
        scrobbleManager.onPlaybackStarted(
            artistName = metadata?.artist?.toString(),
            trackName = metadata?.title?.toString(),
            releaseName = metadata?.albumTitle?.toString(),
            trackNumber = metadata?.trackNumber,
            durationMs = metadata?.durationMs ?: 0
        )
    }

    private fun startScrobblePositionPolling(player: Player) {
        scrobblePollingJob?.cancel()
        scrobblePollingJob = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                val position = player.currentPosition
                scrobbleManager.onPlaybackPositionUpdated(position)
                delay(1000)
            }
        }
    }

    private fun stopScrobblePositionPolling() {
        scrobblePollingJob?.cancel()
        scrobblePollingJob = null
    }

    private fun buildShuffleButton(shuffleEnabled: Boolean): CommandButton =
        CommandButton.Builder(if (shuffleEnabled) CommandButton.ICON_SHUFFLE_ON else CommandButton.ICON_SHUFFLE_OFF)
            .setSessionCommand(SessionCommand(AutoLibraryCallback.TOGGLE_SHUFFLE_ACTION, Bundle.EMPTY))
            .setDisplayName(if (shuffleEnabled) "Shuffle on" else "Shuffle off")
            .build()

    override fun onGetSession(
        controllerInfo: MediaSession.ControllerInfo
    ): MediaLibrarySession? {
        Log.d(TAG, "onGetSession: pkg=${controllerInfo.packageName}, session=$mediaLibrarySession")
        return mediaLibrarySession
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy")
        stopScrobblePositionPolling()
        mediaLibrarySession?.run {
            player.release()
            release()
            mediaLibrarySession = null
        }
        serviceScope.cancel()
        super.onDestroy()
    }
}
