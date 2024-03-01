@file:OptIn(ExperimentalFoundationApi::class)

package com.chaddy50.musicapp.views

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.chaddy50.musicapp.components.EntityCard
import com.chaddy50.musicapp.components.TopBar
import com.chaddy50.musicapp.data.MusicDatabase
import com.chaddy50.musicapp.navigation.NavigationHost
import com.chaddy50.musicapp.navigation.Screen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Home(
    musicDatabase: MusicDatabase
) {
    Scaffold(
        topBar = { TopBar("Home") }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            if (ActivityCompat.checkSelfPermission(
                    LocalContext.current,
                    Manifest.permission.READ_MEDIA_AUDIO
                ) == PackageManager.PERMISSION_DENIED
            ) {
                Text("Go to settings to give this app permission to access your audio files.")
            } else {
                val context = LocalContext.current
                val views = listOf("Genre", "Album", "Artist")
                val pagerState = rememberPagerState(
                    initialPage = 0,
                    initialPageOffsetFraction = 0f,
                    pageCount = { 3 },
                )
                val scope = rememberCoroutineScope()

                PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                    views.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = title,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                }
                TabContent(pagerState, context, musicDatabase)
            }
        }
    }
}

private fun getScreenRouteFromTitle(title: String): String {
    return when (title) {
        "Genre" -> Screen.GenreScreen.route
        "Album" -> Screen.AlbumScreen.route
        "Artist" -> Screen.ArtistScreen.route
        "Track" -> Screen.TrackScreen.route
        else -> ""
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TabContent(
    pagerState: PagerState,
    context: Context,
    musicDatabase: MusicDatabase
) {
    HorizontalPager(pagerState) { index ->
        when (index) {
            0 -> Genres(context, musicDatabase)
            1 -> Albums(context, musicDatabase)
            2 -> Artists(context, musicDatabase)
        }
    }
}