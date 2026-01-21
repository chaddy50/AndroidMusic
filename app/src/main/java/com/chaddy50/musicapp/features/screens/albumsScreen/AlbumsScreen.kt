package com.chaddy50.musicapp.features.screens.albumsScreen

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
import com.chaddy50.musicapp.navigation.MusicAppScreen
import com.chaddy50.musicapp.ui.composables.CleanUpWhenNavigatingBackEffect
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityHeader
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

object AlbumsScreen: MusicAppScreen {
    override val route = "albums_screen"

    @Composable
    override fun Content(
        viewModel: MusicAppViewModel,
        navController: NavController,
        backStackEntry: NavBackStackEntry,
        onTitleChanged: (title: String) -> Unit,
    ) {
        val selectedSubGenreId by viewModel.selectedSubGenreId.collectAsStateWithLifecycle()

        CleanUpWhenNavigatingBackEffect(
            navController,
            route,
            {
                if (selectedSubGenreId != null) {
                    viewModel.updateSelectedSubGenre(null)
                } else {
                    viewModel.updateSelectedAlbumArtist(null)
                }
            }
        )

        val stateHolder = rememberAlbumsScreenState(viewModel)
        val uiState by stateHolder.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(uiState.screenTitle, uiState.isLoading) {
            if (!uiState.isLoading) {
                onTitleChanged(uiState.screenTitle)
            }
        }

        EntityScreen(
            uiState.isLoading,
            {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        EntityHeader(viewModel, EntityType.AlbumArtist)
                    }

                    items(uiState.albums) { album ->
                        AlbumCard(
                            album,
                            viewModel,
                            navController,
                        )
                    }
                }

            }
        )
    }
}