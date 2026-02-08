package com.chaddy50.musicapp.ui.modalSheets.nowPlayingSheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.media3.common.MediaItem
import com.chaddy50.musicapp.ui.modalSheets.nowPlayingSheet.composables.AlbumArtwork
import com.chaddy50.musicapp.ui.modalSheets.nowPlayingSheet.composables.PlaybackControls
import com.chaddy50.musicapp.ui.modalSheets.nowPlayingSheet.composables.ProgressBar
import com.chaddy50.musicapp.ui.modalSheets.nowPlayingSheet.composables.TopBar
import com.chaddy50.musicapp.ui.modalSheets.nowPlayingSheet.composables.TrackInfo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun NowPlayingSheet(
    currentTrack: MediaItem?,
    isPlaying: Boolean,
    playbackPosition: Long,
    durationMs: Long,
    isShuffleModeEnabled: Boolean,
    onShuffleToggled: () -> Unit,
    onPlayPause: () -> Unit,
    onSkipToPreviousTrack: () -> Unit,
    onSkipToNextTrack: () -> Unit,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        windowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            TopBar({
                coroutineScope.launch {
                    sheetState.hide()
                    onDismiss()
                }
            })

            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                AlbumArtwork(currentTrack)

                TrackInfo(currentTrack)

                ProgressBar(
                    playbackPosition,
                    durationMs
                )

                PlaybackControls(
                    isPlaying,
                    isShuffleModeEnabled,
                    onShuffleToggled,
                    onPlayPause,
                    onSkipToPreviousTrack,
                    onSkipToNextTrack
                )
            }
        }
    }
}