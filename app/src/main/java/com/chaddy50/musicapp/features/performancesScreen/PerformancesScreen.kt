package com.chaddy50.musicapp.features.performancesScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.chaddy50.musicapp.features.tracksScreen.TracksScreen
import com.chaddy50.musicapp.navigation.MusicAppScreen
import com.chaddy50.musicapp.ui.composables.CleanUpWhenNavigatingBackEffect
import com.chaddy50.musicapp.ui.composables.EntityCard
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

object PerformancesScreen : MusicAppScreen {
    override val route = "performances_screen"

    @Composable
    override fun Content(
        viewModel: MusicAppViewModel,
        navController: NavController,
        backStackEntry: NavBackStackEntry
    ) {
        CleanUpWhenNavigatingBackEffect(
            navController,
            route,
            {
                viewModel.updateSelectedAlbum(null)
            }
        )

        val albumId = viewModel.selectedAlbumId.collectAsStateWithLifecycle()

        val stateHolder = rememberPerformancesScreenState(albumId.value)
        val uiState by stateHolder.uiState.collectAsStateWithLifecycle()

        EntityScreen(
            viewModel,
            navController,
            EntityType.Album,
            uiState.screenTitle,
            uiState.isLoading,
            {
                uiState.performances.forEach { performance ->
                    EntityCard(
                        "${performance.year} - ${performance.artistName}",
                        {
                            viewModel.updateSelectedPerformance(performance.id)

                            navController.navigate(TracksScreen.route)
                        }
                    )
                }
            }
        )
    }
}