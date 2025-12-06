package com.chaddy50.musicapp.navigation

import androidx.compose.runtime.Composable
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
        startDestination = GenresScreen.route
    ) {
        screens.forEach { screen ->
            composable(
                route = screen.routeWithArgs,
                arguments = screen.arguments
            ) { backStackEntry ->
                screen.Content(
                    viewModel,
                    navController,
                    backStackEntry
                )
            }
        }
    }
}