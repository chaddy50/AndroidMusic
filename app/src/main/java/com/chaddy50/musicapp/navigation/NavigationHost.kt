package com.chaddy50.musicapp.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chaddy50.musicapp.data.MusicDatabase
import com.chaddy50.musicapp.views.Albums
import com.chaddy50.musicapp.views.Artists
import com.chaddy50.musicapp.views.Genres
import com.chaddy50.musicapp.views.Home
import com.chaddy50.musicapp.views.Tracks

@Composable
fun NavigationHost(
    context: Context,
    musicDatabase: MusicDatabase,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {
        composable(Screen.HomeScreen.route) {
            Home(navController, musicDatabase)
        }
        composable(Screen.GenreScreen.route) {
            Genres(context, musicDatabase, navController)
        }
        composable(
            Screen.AlbumScreen.route + "?artistID={artistID}",
            arguments = listOf(navArgument("artistID") { defaultValue = 0})
        ) {
            Albums(
                context,
                musicDatabase,
                navController,
                it.arguments?.getInt("artistID") ?: 0
            )
        }
        composable(
            Screen.ArtistScreen.route + "?genreID={genreID}",
            arguments = listOf(navArgument("genreID") { defaultValue = 0})
        ) {
            Artists(
                context,
                musicDatabase,
                navController,
                it.arguments?.getInt("genreID") ?: 0)
        }
        composable(
            Screen.TrackScreen.route + "?albumID={albumID}",
            arguments = listOf(navArgument("albumID") { defaultValue = 0})
            ) {
            Tracks(
                context,
                musicDatabase,
                navController,
                it.arguments?.getInt("albumID") ?: 0
            )
        }
    }
}