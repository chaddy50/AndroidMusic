package com.chaddy50.musicapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chaddy50.musicapp.viewModel.MusicAppViewModel
import com.chaddy50.musicapp.features.albumsScreen.AlbumsScreen
import com.chaddy50.musicapp.features.artistsScreen.ArtistsScreen
import com.chaddy50.musicapp.features.genresScreen.GenresScreen
import com.chaddy50.musicapp.features.performancesScreen.PerformancesScreen
import com.chaddy50.musicapp.features.subGenresScreen.SubGenresScreen
import com.chaddy50.musicapp.features.tracksScreen.TracksScreen
import com.chaddy50.musicapp.ui.composables.MusicScannerProgressBar

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
    NavHost(
        navController = navController,
        startDestination = GenresScreen.route,
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        screens.forEach { screen ->
            composable(
                route = screen.routeWithArgs,
                arguments = screen.arguments
            ) { backStackEntry ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        screen.Content(
                            viewModel,
                            navController,
                            backStackEntry
                        )
                    }

                    MusicScannerProgressBar(viewModel)
                }

            }
        }
    }
}