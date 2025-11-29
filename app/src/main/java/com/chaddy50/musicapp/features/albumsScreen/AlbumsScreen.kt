package com.chaddy50.musicapp.features.albumsScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.chaddy50.musicapp.navigation.MusicAppScreen

object AlbumsScreen: MusicAppScreen {
    override val route = "albums_screen"
    const val ARG_ARTIST_ID = "artistId" // Constant for the artist ID key

    override val arguments: List<NamedNavArgument> = listOf(
        navArgument(ARG_ARTIST_ID) {
            type = NavType.IntType
            defaultValue = 0 // Default to 0, meaning "all albums"
        }
    )

    @Composable
    override fun Content(navController: NavController, backStackEntry: NavBackStackEntry) {
        val artistId = backStackEntry.arguments?.getInt(ARG_ARTIST_ID) ?: 0
        val stateHolder = rememberAlbumsScreenState(artistId)
        val uiState by stateHolder.uiState.collectAsStateWithLifecycle()

        AlbumList(
            navController,
            uiState
        )
    }
}