package com.chaddy50.musicapp.ui.screens

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
import androidx.navigation.NavController
import com.chaddy50.musicapp.ui.screens.genresScreen.GenresScreen
import com.chaddy50.musicapp.ui.screens.playlistsScreen.PlaylistsScreen
import com.chaddy50.musicapp.viewModel.MusicAppViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: MusicAppViewModel,
    navController: NavController,
    onTitleChanged: (String) -> Unit,
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
            beyondViewportPageCount = 1,
            modifier = Modifier
                .fillMaxSize()
                .imePadding(),
        ) { page ->
            when (page) {
                0 -> GenresScreen(viewModel, navController)
                1 -> PlaylistsScreen(viewModel, navController)
            }
        }
    }
}
