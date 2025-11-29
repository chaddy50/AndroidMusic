package com.chaddy50.musicapp.features.artistsScreen

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.chaddy50.musicapp.navigation.MusicAppScreen

object ArtistsScreen: MusicAppScreen {
    override val route = "artists_screen"
    const val ARG_GENRE_ID = "genreId"

    override val arguments = listOf(
        navArgument(ARG_GENRE_ID) {
            type = NavType.IntType
            defaultValue = 0
        }
    )

    @Composable
    override fun Content(
        navController: NavController,
        backStackEntry: NavBackStackEntry
    ) {
        val genreId = backStackEntry.arguments?.getInt(ARG_GENRE_ID) ?: 0
        ArtistList(navController, genreId)
    }
}