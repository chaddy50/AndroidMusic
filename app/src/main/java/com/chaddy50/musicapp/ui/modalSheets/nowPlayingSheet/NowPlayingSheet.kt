package com.chaddy50.musicapp.ui.modalSheets.nowPlayingSheet

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import com.chaddy50.musicapp.ui.modalSheets.nowPlayingSheet.composables.AlbumArtwork
import com.chaddy50.musicapp.ui.modalSheets.nowPlayingSheet.composables.PlaybackControls
import com.chaddy50.musicapp.ui.modalSheets.nowPlayingSheet.composables.ProgressBar
import com.chaddy50.musicapp.ui.modalSheets.nowPlayingSheet.composables.QueueView
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
    queue: List<MediaItem>,
    currentTrackIndex: Int,
    onShuffleToggled: () -> Unit,
    onPlayPause: () -> Unit,
    onSkipToPreviousTrack: () -> Unit,
    onSkipToNextTrack: () -> Unit,
    onSkipToTrack: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val pagerState = rememberPagerState(pageCount = { 2 })
    val isShowingQueue = pagerState.currentPage == 1
    val colorScheme = getColorSchemeForAlbumArtwork(
        currentTrack?.mediaMetadata?.artworkUri?.let { "file://$it".toUri() },
        MaterialTheme.colorScheme
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = null,
        windowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = colorScheme.surface,
        modifier = Modifier
            .fillMaxSize()
    ) {
        MaterialTheme(colorScheme) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.statusBars)
                ) {
                    TopBar(
                        onDismiss = {
                            coroutineScope.launch {
                                sheetState.hide()
                                onDismiss()
                            }
                        },
                        isShowingQueue = isShowingQueue,
                        onQueueToggled = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(if (isShowingQueue) 0 else 1)
                            }
                        }
                    )

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { page ->
                        if (page == 1) {
                            QueueView(queue, currentTrackIndex, onSkipToTrack)
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
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
            }
        }
    }
}