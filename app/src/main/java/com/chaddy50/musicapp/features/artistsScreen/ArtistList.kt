package com.chaddy50.musicapp.features.artistsScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.chaddy50.musicapp.features.albumsScreen.AlbumsScreen
import com.chaddy50.musicapp.features.subGenresScreen.SubGenresScreen
import com.chaddy50.musicapp.ui.composables.TopBar
import com.chaddy50.musicapp.ui.composables.EntityCard
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

@Composable
fun ArtistList(
    viewModel: MusicAppViewModel,
    navController: NavController,
    uiState: ArtistsScreenUiState,
) {
    val selectedGenreId = viewModel.selectedGenreId.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopBar(
                true,
                uiState.screenTitle,
                navController
            )
        }
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator()
        }
        else {
            LazyColumn(modifier = Modifier.padding(it)) {
                items(uiState.artists) { artist ->
                    EntityCard(
                        artist.name,
                        {
                            viewModel.updateSelectedAlbumArtist(artist.id)

                            if (selectedGenreId.value == viewModel.classicalGenreId) {
                                navController.navigate(SubGenresScreen.route)
                            }
                            else {
                                navController.navigate(AlbumsScreen.route)
                            }
                        }
                    )
                }
            }
        }
    }
}