package com.chaddy50.musicapp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.chaddy50.musicapp.data.entity.Genre
import com.chaddy50.musicapp.navigation.AlbumsRoute
import com.chaddy50.musicapp.navigation.ArtistsRoute
import com.chaddy50.musicapp.navigation.HomeRoute
import com.chaddy50.musicapp.navigation.PerformancesRoute
import com.chaddy50.musicapp.navigation.PlaylistTracksRoute
import com.chaddy50.musicapp.navigation.TracksRoute
import com.chaddy50.musicapp.ui.composables.MusicScannerProgressBar
import com.chaddy50.musicapp.ui.composables.SubGenreFilterButton
import com.chaddy50.musicapp.ui.composables.TopBar
import com.chaddy50.musicapp.ui.composables.nowPlayingBar.NowPlayingBar
import com.chaddy50.musicapp.ui.modalSheets.nowPlayingSheet.NowPlayingSheet
import com.chaddy50.musicapp.ui.screens.HomeScreen
import com.chaddy50.musicapp.ui.screens.albumsScreen.AlbumsScreen
import com.chaddy50.musicapp.ui.screens.artistsScreen.ArtistsScreen
import com.chaddy50.musicapp.ui.screens.performancesScreen.PerformancesScreen
import com.chaddy50.musicapp.ui.screens.playlistTracksScreen.PlaylistTracksScreen
import com.chaddy50.musicapp.ui.screens.tracksScreen.TracksScreen
import com.chaddy50.musicapp.viewModel.MusicAppViewModel
import kotlinx.coroutines.flow.flowOf

@Composable
fun NavigationHost(
    viewModel: MusicAppViewModel,
    navController: NavHostController = rememberNavController(),
) {
    var shouldShowNowPlayingSheet by remember { mutableStateOf(false) }
    var topBarTitle by remember { mutableStateOf("") }
    val currentTrack by viewModel.nowPlayingState.currentTrack.collectAsStateWithLifecycle()
    val isPlaying by viewModel.nowPlayingState.isPlaying.collectAsStateWithLifecycle()
    val playbackPosition by viewModel.nowPlayingState.playbackPosition.collectAsStateWithLifecycle()
    val durationMs = currentTrack?.mediaMetadata?.durationMs ?: 0
    val isShuffleModeEnabled by viewModel.nowPlayingState.isShuffleModeEnabled.collectAsStateWithLifecycle()
    val queue by viewModel.nowPlayingState.queue.collectAsStateWithLifecycle()
    val currentTrackIndex by viewModel.nowPlayingState.currentTrackIndex.collectAsStateWithLifecycle()
    val onPlayPause = { viewModel.nowPlayingState.playOrPause() }
    val onSkipToTrack = { index: Int -> viewModel.nowPlayingState.skipToTrack(index) }
    val onSkipToPreviousTrack = viewModel.nowPlayingState.skipPrevious()
    val onSkipToNextTrack = viewModel.nowPlayingState.skipNext()
    val onShuffleToggled = { viewModel.nowPlayingState.toggleShuffleMode() }

    val isScanInProgress by viewModel.isScanInProgress.collectAsStateWithLifecycle()
    val scanProgress by viewModel.scanProgress.collectAsStateWithLifecycle()

    // Sub-genre filter button: extract args from current back stack entry when on AlbumsRoute
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStackEntry?.destination?.route
    val isOnAlbumsRoute = currentRoute?.contains("AlbumsRoute") == true

    val albumsRouteArgs = remember(currentBackStackEntry) {
        if (isOnAlbumsRoute) {
            try {
                currentBackStackEntry?.toRoute<AlbumsRoute>()
            } catch (_: Exception) {
                null
            }
        } else null
    }

    val subGenres by remember(albumsRouteArgs) {
        if (albumsRouteArgs != null) {
            viewModel.getSubGenresForAlbumArtist(albumsRouteArgs.genreId, albumsRouteArgs.albumArtistId)
        } else {
            flowOf(emptyList<Genre>())
        }
    }.collectAsState(emptyList())

    val selectedSubGenreId by viewModel.selectedSubGenreId.collectAsStateWithLifecycle()
    val showFilterButton = subGenres.size > 1 && isOnAlbumsRoute

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    topBarTitle,
                    navController,
                    actions = {
                        if (showFilterButton) {
                            SubGenreFilterButton(
                                subGenres = subGenres,
                                selectedSubGenreId = selectedSubGenreId,
                                onSubGenreSelected = { viewModel.updateSelectedSubGenre(it) }
                            )
                        }
                    }
                )
            },
            bottomBar = {
                Column {
                    MusicScannerProgressBar(isScanInProgress, scanProgress)
                    NowPlayingBar(
                        currentTrack,
                        isPlaying,
                        playbackPosition,
                        durationMs,
                        isShuffleModeEnabled,
                        onPlayPause,
                        onSkipToNextTrack,
                        onShuffleToggled,
                        { shouldShowNowPlayingSheet = true },
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = HomeRoute,
                modifier = Modifier
                    .padding(innerPadding)
                    .imePadding()
            ) {
                composable<HomeRoute> {
                    HomeScreen(
                        viewModel = viewModel,
                        navController = navController,
                        onTitleChanged = { topBarTitle = it },
                    )
                }
                composable<ArtistsRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<ArtistsRoute>()
                    ArtistsScreen(
                        genreId = route.genreId,
                        viewModel = viewModel,
                        navController = navController,
                        onTitleChanged = { topBarTitle = it },
                    )
                }
                composable<AlbumsRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<AlbumsRoute>()
                    AlbumsScreen(
                        genreId = route.genreId,
                        albumArtistId = route.albumArtistId,
                        viewModel = viewModel,
                        navController = navController,
                        onTitleChanged = { topBarTitle = it },
                    )
                }
                composable<PerformancesRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<PerformancesRoute>()
                    PerformancesScreen(
                        genreId = route.genreId,
                        albumId = route.albumId,
                        viewModel = viewModel,
                        navController = navController,
                        onTitleChanged = { topBarTitle = it },
                    )
                }
                composable<TracksRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<TracksRoute>()
                    TracksScreen(
                        genreId = route.genreId,
                        albumId = route.albumId,
                        performanceId = if (route.performanceId == -1L) null else route.performanceId,
                        viewModel = viewModel,
                        onTitleChanged = { topBarTitle = it },
                    )
                }
                composable<PlaylistTracksRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<PlaylistTracksRoute>()
                    PlaylistTracksScreen(
                        playlistId = route.playlistId,
                        viewModel = viewModel,
                        onTitleChanged = { topBarTitle = it },
                    )
                }
            }
        }

        if (shouldShowNowPlayingSheet) {
            NowPlayingSheet(
                currentTrack,
                isPlaying,
                playbackPosition,
                durationMs,
                isShuffleModeEnabled,
                queue,
                currentTrackIndex,
                onShuffleToggled,
                onPlayPause,
                onSkipToPreviousTrack,
                onSkipToNextTrack,
                onSkipToTrack,
                { shouldShowNowPlayingSheet = false }
            )
        }
    }
}
