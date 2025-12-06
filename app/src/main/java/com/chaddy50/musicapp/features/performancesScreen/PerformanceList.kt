package com.chaddy50.musicapp.features.performancesScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.chaddy50.musicapp.features.tracksScreen.TracksScreen
import com.chaddy50.musicapp.ui.composables.EntityCard
import com.chaddy50.musicapp.ui.composables.TopBar
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

@Composable
fun PerformanceList(
    viewModel: MusicAppViewModel,
    navController: NavController,
    uiState: PerformanceScreenUiState
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
        if (uiState.isLoading) {
            CircularProgressIndicator()
        }
        else {
            LazyColumn(Modifier.padding(it)) {
                items(uiState.performances) { performance ->
                    EntityCard(
                        "${performance.year} - ${performance.artistName}",
                        {
                            viewModel.updateSelectedPerformance(performance.id)

                            navController.navigate(TracksScreen.route)
                        }
                    )
                }
            }
        }
    }
}