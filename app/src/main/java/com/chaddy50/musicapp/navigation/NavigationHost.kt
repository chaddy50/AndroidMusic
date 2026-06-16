package com.chaddy50.musicapp.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
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
import com.chaddy50.musicapp.data.scanner.LibraryScanViewModel
import com.chaddy50.musicapp.ui.composables.nowPlayingBar.PlaybackViewModel
import com.chaddy50.musicapp.ui.screens.albumsScreen.AlbumsScreenUiState
import com.chaddy50.musicapp.ui.screens.albumsScreen.AlbumsScreenViewModel
import com.chaddy50.musicapp.ui.screens.playlistsScreen.PlaylistViewModel
import com.chaddy50.musicapp.ui.screens.settingsScreen.SettingsScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationHost(
    playbackViewModel: PlaybackViewModel,
    playlistViewModel: PlaylistViewModel,
    libraryScanViewModel: LibraryScanViewModel,
    navController: NavHostController = rememberNavController(),
) {
    var shouldShowNowPlayingSheet by remember { mutableStateOf(false) }
    var homeScreenTitle by remember { mutableStateOf("Library") }
    val currentTrack by playbackViewModel.nowPlayingState.currentTrack.collectAsStateWithLifecycle()
    val isPlaying by playbackViewModel.nowPlayingState.isPlaying.collectAsStateWithLifecycle()
    val playbackPosition by playbackViewModel.nowPlayingState.playbackPosition.collectAsStateWithLifecycle()
    val durationMs = currentTrack?.mediaMetadata?.durationMs ?: 0
    val isShuffleModeEnabled by playbackViewModel.nowPlayingState.isShuffleModeEnabled.collectAsStateWithLifecycle()
    val queue by playbackViewModel.nowPlayingState.queue.collectAsStateWithLifecycle()
    val currentTrackIndex by playbackViewModel.nowPlayingState.currentTrackIndex.collectAsStateWithLifecycle()
    val onPlayPause = { playbackViewModel.nowPlayingState.playOrPause() }
    val onSkipToTrack = { index: Int -> playbackViewModel.nowPlayingState.skipToTrack(index) }
    val onSkipToPreviousTrack = { playbackViewModel.nowPlayingState.skipPrevious() }
    val onSkipToNextTrack = { playbackViewModel.nowPlayingState.skipNext() }
    val onShuffleToggled = { playbackViewModel.nowPlayingState.toggleShuffleMode() }
    val onSeek = { positionMs: Long -> playbackViewModel.nowPlayingState.seekTo(positionMs) }

    val isScanInProgress by libraryScanViewModel.isScanInProgress.collectAsStateWithLifecycle()
    val scanProgress by libraryScanViewModel.scanProgress.collectAsStateWithLifecycle()

    // Derive top bar title and sub-genre filter state from current route
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val destination = currentBackStackEntry?.destination
    val isOnAlbumsRoute = destination?.hasRoute<AlbumsRoute>() == true

    val albumsScreenViewModel: AlbumsScreenViewModel? = if (isOnAlbumsRoute && currentBackStackEntry != null) {
        hiltViewModel(currentBackStackEntry!!)
    } else null

    val albumsUiState by albumsScreenViewModel?.uiState?.collectAsStateWithLifecycle()
        ?: remember { mutableStateOf(AlbumsScreenUiState()) }
    val subGenres by albumsScreenViewModel?.subGenres?.collectAsStateWithLifecycle()
        ?: remember { mutableStateOf(emptyList()) }
    val selectedSubGenreId by albumsScreenViewModel?.selectedSubGenreId?.collectAsStateWithLifecycle()
        ?: remember { mutableStateOf(null) }
    val showFilterButton = subGenres.size > 1 && isOnAlbumsRoute

    val isOnHomeRoute = destination?.hasRoute<HomeRoute>() == true
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val topBarTitle = when {
        destination == null -> ""
        destination.hasRoute<HomeRoute>() -> homeScreenTitle
        destination.hasRoute<AlbumsRoute>() -> albumsUiState.screenTitle
        destination.hasRoute<ArtistsRoute>() -> currentBackStackEntry?.toRoute<ArtistsRoute>()?.title ?: ""
        destination.hasRoute<PerformancesRoute>() -> currentBackStackEntry?.toRoute<PerformancesRoute>()?.title ?: ""
        destination.hasRoute<PlaylistTracksRoute>() -> currentBackStackEntry?.toRoute<PlaylistTracksRoute>()?.title ?: ""
        destination.hasRoute<TracksRoute>() -> currentBackStackEntry?.toRoute<TracksRoute>()?.title ?: ""
        destination.hasRoute<SettingsRoute>() -> "Settings"
        else -> ""
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    topBarTitle,
                    navController,
                    scrollBehavior = scrollBehavior,
                    actions = {
                        if (showFilterButton) {
                            SubGenreFilterButton(
                                subGenres = subGenres,
                                selectedSubGenreId = selectedSubGenreId,
                                onSubGenreSelected = { albumsScreenViewModel?.updateSelectedSubGenreId(it) }
                            )
                        }
                        if (isOnHomeRoute) {
                            IconButton(onClick = { navController.navigate(SettingsRoute) }) {
                                Icon(
                                    imageVector = Icons.Filled.Settings,
                                    contentDescription = "Settings"
                                )
                            }
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
                .nestedScroll(scrollBehavior.nestedScrollConnection)
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
                        playbackViewModel = playbackViewModel,
                        playlistViewModel = playlistViewModel,
                        navController = navController,
                        onTitleChanged = { homeScreenTitle = it },
                    )
                }
                composable<ArtistsRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<ArtistsRoute>()
                    ArtistsScreen(
                        genreId = route.genreId,
                        playbackViewModel = playbackViewModel,
                        playlistViewModel = playlistViewModel,
                        navController = navController,
                    )
                }
                composable<AlbumsRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<AlbumsRoute>()
                    AlbumsScreen(
                        genreId = route.genreId,
                        albumArtistId = route.albumArtistId,
                        playbackViewModel = playbackViewModel,
                        playlistViewModel = playlistViewModel,
                        navController = navController,
                    )
                }
                composable<PerformancesRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<PerformancesRoute>()
                    PerformancesScreen(
                        genreId = route.genreId,
                        albumId = route.albumId,
                        playbackViewModel = playbackViewModel,
                        playlistViewModel = playlistViewModel,
                        navController = navController,
                    )
                }
                composable<TracksRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<TracksRoute>()
                    TracksScreen(
                        genreId = route.genreId,
                        albumId = route.albumId,
                        performanceId = if (route.performanceId == -1L) null else route.performanceId,
                        playbackViewModel = playbackViewModel,
                        playlistViewModel = playlistViewModel,
                    )
                }
                composable<PlaylistTracksRoute> { backStackEntry ->
                    val route = backStackEntry.toRoute<PlaylistTracksRoute>()
                    PlaylistTracksScreen(
                        playlistId = route.playlistId,
                        playbackViewModel = playbackViewModel,
                        playlistViewModel = playlistViewModel,
                    )
                }
                composable<SettingsRoute> {
                    SettingsScreen()
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
                onSeek,
                { shouldShowNowPlayingSheet = false }
            )
        }
    }
}
