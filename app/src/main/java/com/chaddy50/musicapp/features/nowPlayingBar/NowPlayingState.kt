package com.chaddy50.musicapp.features.nowPlayingBar

import android.app.Application
import android.content.ComponentName
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.chaddy50.musicapp.services.PlaybackService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NowPlayingState(
    application: Application,
    private val scope: CoroutineScope
) : Player.Listener {
    private val _currentTrack = MutableStateFlow<MediaItem?>(null)
    val currentTrack = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    private val _playbackPosition = MutableStateFlow(0L)
    val playbackPosition = _playbackPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration = _duration.asStateFlow()

    private val _isShuffleModeEnabled = MutableStateFlow(false)
    val isShuffleModeEnabled = _isShuffleModeEnabled.asStateFlow()

    private var controllerFuture: ListenableFuture<MediaController>
    val controller: MediaController? get() = if (controllerFuture.isDone) controllerFuture.get() else null

    private var positionUpdateJob: Job? = null

    init {
        val sessionToken = SessionToken(application,
            ComponentName(application, PlaybackService::class.java)
        )
        controllerFuture =
            MediaController.Builder(application, sessionToken).buildAsync()
        controllerFuture.addListener(
            {
                // Controller is ready, attach a listener so that we get updates about the controller's state
                controller?.addListener(this)
                updateState()
            },
            MoreExecutors.directExecutor()
        )
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        _currentTrack.value = mediaItem
        updateState()
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _isPlaying.value = isPlaying
        if (isPlaying) {
            startPositionUpdates()
        } else {
            stopPositionUpdates()
        }
    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
        super.onShuffleModeEnabledChanged(shuffleModeEnabled)
        _isShuffleModeEnabled.value = shuffleModeEnabled
    }

    fun play() = controller?.play()
    fun pause() = controller?.pause()
    fun seekTo(position: Long) = controller?.seekTo(position)
    fun skipNext() = controller?.seekToNextMediaItem()
    fun skipPrevious() = controller?.seekToPreviousMediaItem()

    fun playOrPause() {
        if (_isPlaying.value) {
            pause()
        } else {
            play()
        }
    }

    fun toggleShuffleMode() {
        controller?.let {
            it.shuffleModeEnabled = !it.shuffleModeEnabled
        }
    }

    private fun startPositionUpdates() {
        stopPositionUpdates() // Ensure only one job is running
        positionUpdateJob = scope.launch {
            while (true) {
                _playbackPosition.value = controller?.currentPosition ?: 0
                delay(1000) // Update every second
            }
        }
    }

    private fun stopPositionUpdates() {
        positionUpdateJob?.cancel()
        positionUpdateJob = null
    }

    private fun updateState() {
        controller?.let {
            _currentTrack.value = it.currentMediaItem
            _isPlaying.value = it.isPlaying
            _duration.value = _currentTrack.value?.mediaMetadata?.durationMs ?: 0L
            _playbackPosition.value = it.currentPosition
            _isShuffleModeEnabled.value = it.shuffleModeEnabled
            if (it.isPlaying) startPositionUpdates() else stopPositionUpdates()
        }
    }

    fun release() {
        controller?.removeListener(this)
        MediaController.releaseFuture(controllerFuture)
        stopPositionUpdates()
    }
}