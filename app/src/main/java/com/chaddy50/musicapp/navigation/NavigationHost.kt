package com.chaddy50.musicapp.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chaddy50.musicapp.viewModel.MusicAppViewModel
import com.chaddy50.musicapp.views.Albums
import com.chaddy50.musicapp.views.Artists
import com.chaddy50.musicapp.views.Genres
import com.chaddy50.musicapp.views.Tracks

@Composable
fun NavigationHost(
    context: Context,
    viewModel: MusicAppViewModel,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.GenreScreen.route
    ) {
        composable(Screen.GenreScreen.route) {
            Genres(
                viewModel,
                navController
            )
        }
        composable(
            Screen.AlbumScreen.route + "?artistId={artistId}",
            arguments = listOf(navArgument("artistId") { defaultValue = ""})
        ) {
            Albums(
                viewModel,
                navController,
                it.arguments?.getInt("artistId") ?: -1
            )
        }
        composable(
            Screen.ArtistScreen.route + "?genreId={genreId}",
            arguments = listOf(navArgument("genreId") { defaultValue = 0})
        ) {
            Artists(
                viewModel,
                navController,
                it.arguments?.getInt("genreId") ?: 0)
        }
        composable(
            Screen.TrackScreen.route + "?albumId={albumId}",
            arguments = listOf(navArgument("albumId") { defaultValue = 0})
            ) {
            Tracks(
                viewModel,
                navController,
                it.arguments?.getInt("albumId") ?: 0
            )
        }
    }
}