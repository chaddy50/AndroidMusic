package com.chaddy50.musicapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.chaddy50.musicapp.ui.screens.genresScreen.GenresScreen
import com.chaddy50.musicapp.ui.screens.playlistsScreen.PlaylistsScreen
import com.chaddy50.musicapp.viewModel.MusicAppViewModel
import kotlinx.coroutines.launch

object HomeScreen : MusicAppScreen {
    override val route = "home"

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content(
        viewModel: MusicAppViewModel,
        navController: NavController,
        backStackEntry: NavBackStackEntry,
        onTitleChanged: (String) -> Unit
    ) {
        val pagerState = rememberPagerState(pageCount = { 2 })
        val coroutineScope = rememberCoroutineScope()

        LaunchedEffect(pagerState.currentPage) {
            if (pagerState.currentPage == 0) {
                onTitleChanged("Library")
            } else if (pagerState.currentPage == 1) {
                onTitleChanged("Playlists")
            }
        }

        Column {
            TabRow(selectedTabIndex = pagerState.currentPage) {
                Tab(
                    selected = pagerState.currentPage == 0,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                    icon = { Icon(Icons.Filled.LibraryMusic, contentDescription = null) },
                )
                Tab(
                    selected = pagerState.currentPage == 1,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                    icon = { Icon(Icons.AutoMirrored.Filled.QueueMusic, contentDescription = null) },
                )
            }

            HorizontalPager(
                state = pagerState,
                beyondBoundsPageCount = 1,
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
            ) { page ->
                when (page) {
                    0 -> GenresScreen.Content(viewModel, navController, backStackEntry, onTitleChanged)
                    1 -> PlaylistsScreen.Content(viewModel, navController, backStackEntry, onTitleChanged)
                }
            }
        }
    }
}