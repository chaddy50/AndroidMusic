package com.chaddy50.musicapp.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chaddy50.musicapp.features.nowPlayingBar.NowPlayingBar
import com.chaddy50.musicapp.features.screens.albumsScreen.AlbumsScreen
import com.chaddy50.musicapp.features.screens.artistsScreen.ArtistsScreen
import com.chaddy50.musicapp.features.screens.genresScreen.GenresScreen
import com.chaddy50.musicapp.features.screens.performancesScreen.PerformancesScreen
import com.chaddy50.musicapp.features.screens.subGenresScreen.SubGenresScreen
import com.chaddy50.musicapp.features.screens.tracksScreen.TracksScreen
import com.chaddy50.musicapp.ui.composables.MusicScannerProgressBar
import com.chaddy50.musicapp.ui.composables.TopBar
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

@Composable
fun NavigationHost(
    viewModel: MusicAppViewModel,
    navController: NavHostController = rememberNavController(),
) {
    val screens = listOf(
        GenresScreen,
        ArtistsScreen,
        AlbumsScreen,
        TracksScreen,
        SubGenresScreen,
        PerformancesScreen,
    )

    var topBarTitle by remember { mutableStateOf("") }
    val currentTrack by viewModel.nowPlayingState.currentTrack.collectAsStateWithLifecycle()
    val isPlaying by viewModel.nowPlayingState.isPlaying.collectAsStateWithLifecycle()
    val playbackPosition by viewModel.nowPlayingState.playbackPosition.collectAsStateWithLifecycle()
    val duration = currentTrack?.mediaMetadata?.durationMs ?: 0

    Scaffold(
        topBar = {
            TopBar(
                topBarTitle,
                navController
            )
        },
        bottomBar = {
            Column {
                MusicScannerProgressBar(viewModel)
                NowPlayingBar(
                    currentTrack,
                    isPlaying,
                    playbackPosition,
                    duration,
                    { viewModel.nowPlayingState.playOrPause() },
                    { viewModel.nowPlayingState.skipNext() }
                )
            }
        },
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = GenresScreen.route,
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
        ) {
            screens.forEach { screen ->
                composable(
                    route = screen.routeWithArgs,
                    arguments = screen.arguments
                ) { backStackEntry ->
                    screen.Content(
                        viewModel,
                        navController,
                        backStackEntry,
                        { newTitle ->
                            topBarTitle = newTitle
                        }
                    )
                }
            }
        }
    }
}