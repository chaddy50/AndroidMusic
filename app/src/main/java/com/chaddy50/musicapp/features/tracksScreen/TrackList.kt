package com.chaddy50.musicapp.features.tracksScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.chaddy50.musicapp.ui.composables.TopBar

@Composable
fun TrackList(
    navController: NavController,
    uiState: TracksScreenUiState,
) {
    Scaffold(
        topBar = {
            TopBar(
                true,
                uiState.screenTitle,
                navController
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {

//            if (albumId != 0) {
//                AlbumHeader(albumId, viewModel)
//            }

            uiState.tracks.forEach { track ->
                TrackCard(track)
            }
        }
    }
}