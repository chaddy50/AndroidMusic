package com.chaddy50.musicapp.ui.screens.albumsScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.ui.screens.performancesScreen.PerformancesScreen
import com.chaddy50.musicapp.ui.screens.tracksScreen.TracksScreen
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlbumCard(
    album: Album,
    viewModel: MusicAppViewModel,
    navController: NavController,
    onLongPress: (() -> Unit)? = null,
) {
    val selectedGenreId by viewModel.selectedGenreId.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(4.dp)
            .combinedClickable(
                onClick = {
                    viewModel.updateSelectedAlbum(album.id)
                    if (selectedGenreId == viewModel.classicalGenreId) {
                        navController.navigate(PerformancesScreen.route)
                    } else {
                        navController.navigate(TracksScreen.route)
                    }
                },
                onLongClick = onLongPress,
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selectedGenreId != viewModel.classicalGenreId) {
                    Column {
                        AsyncImage(
                            model = album.artworkPath,
                            contentDescription = "${album.title} Artwork",
                            modifier = Modifier.aspectRatio(1f),
                        )
                    }
                }
                Column(modifier = Modifier.padding(10.dp, 0.dp)) {
                    Text(album.title, style = TextStyle(fontSize = 16.sp))
                    if (selectedGenreId != viewModel.classicalGenreId) {
                        Text(album.year, style = TextStyle(fontSize = 14.sp))
                    }
                }
            }
        }
    }
}