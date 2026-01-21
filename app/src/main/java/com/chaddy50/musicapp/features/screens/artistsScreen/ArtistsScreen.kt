package com.chaddy50.musicapp.features.screens.artistsScreen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.chaddy50.musicapp.features.screens.albumsScreen.AlbumsScreen
import com.chaddy50.musicapp.navigation.MusicAppScreen
import com.chaddy50.musicapp.ui.composables.CleanUpWhenNavigatingBackEffect
import com.chaddy50.musicapp.ui.composables.EntityCard
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityHeader
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

object ArtistsScreen: MusicAppScreen {
    override val route = "artists_screen"

    @Composable
    override fun Content(
        viewModel: MusicAppViewModel,
        navController: NavController,
        backStackEntry: NavBackStackEntry,
        onTitleChanged: (title: String) -> Unit,
    ) {
        CleanUpWhenNavigatingBackEffect(
            navController,
            route,
            { viewModel.updateSelectedGenre(null) }
        )

        val selectedGenreId = viewModel.selectedGenreId.collectAsStateWithLifecycle()
        val stateHolder = rememberArtistsScreenState(selectedGenreId.value)
        val uiState by stateHolder.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(uiState.screenTitle, uiState.isLoading) {
            if (!uiState.isLoading) {
                onTitleChanged(uiState.screenTitle)
            }
        }

        EntityScreen(
            uiState.isLoading,
            {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        EntityHeader(viewModel, EntityType.Genre)
                    }

                    items(uiState.artists) { artist ->
                        EntityCard(
                            artist.name,
                            {
                                viewModel.updateSelectedAlbumArtist(artist.id)
                                navController.navigate(AlbumsScreen.route)
                            }
                        )
                    }
                }

            }
        )
    }
}