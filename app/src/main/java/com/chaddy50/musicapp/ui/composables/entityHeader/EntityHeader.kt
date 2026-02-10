package com.chaddy50.musicapp.ui.composables.entityHeader

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

enum class EntityType {
    Genre,
    SubGenre,
    AlbumArtist,
    Album,
    Artist,
    Performance,
    Track,
    All
}

@Composable
fun EntityHeader(
    viewModel: MusicAppViewModel,
    type: EntityType,
) {
    val stateHolder = rememberEntityHeaderState(
        type,
        viewModel
    )
    val uiState by stateHolder.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        CircularProgressIndicator()
    } else {

        Column(
            modifier = Modifier
                .padding(0.dp, 10.dp, 0.dp, 0.dp)
                .fillMaxSize()
        ) {
            if (uiState.artworkPath != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .height(300.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = uiState.artworkPath,
                        contentDescription = "${uiState.title} Artwork",
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxHeight()
                    )
                }
            }

            Row(modifier = Modifier.padding(10.dp, 3.dp)) {
                Text(uiState.title, style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold))
            }

            if (uiState.subtitle.isNotEmpty()) {
                Row(modifier = Modifier.padding(10.dp, 3.dp)) {
                    Text(uiState.subtitle, style = TextStyle(fontSize = 14.sp))
                }
            }

            if (!uiState.details.isNullOrEmpty()) {
                Row(modifier = Modifier.padding(10.dp, 3.dp)) {
                    Text(uiState.details!!, style = TextStyle(fontSize = 14.sp))
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}