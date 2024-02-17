package com.chaddy50.musicapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chaddy50.musicapp.views.Home
import com.chaddy50.musicapp.views.Albums
import com.chaddy50.musicapp.views.Artists
import com.chaddy50.musicapp.views.Genres
import com.chaddy50.musicapp.views.Tracks

@Composable
fun NavigationHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {
        composable(Screen.HomeScreen.route) {
            Home(navController)
        }
        composable(Screen.GenreScreen.route) {
            Genres()
        }
        composable(Screen.AlbumScreen.route) {
            Albums()
        }
        composable(Screen.ArtistScreen.route) {
            Artists()
        }
        composable(Screen.TrackScreen.route) {
            Tracks()
        }
    }
}