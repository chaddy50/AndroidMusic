package com.chaddy50.musicapp.ui.screens.playlistTracksScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.chaddy50.musicapp.data.entity.Track
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityHeader
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
import com.chaddy50.musicapp.ui.screens.MusicAppScreen
import com.chaddy50.musicapp.ui.screens.tracksScreen.TrackCard
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

object PlaylistTracksScreen : MusicAppScreen {
    override val route = "playlist_tracks_screen"

    @Composable
    override fun Content(
        viewModel: MusicAppViewModel,
        navController: NavController,
        backStackEntry: NavBackStackEntry,
        onTitleChanged: (title: String) -> Unit,
    ) {
        val playlistId by viewModel.selectedPlaylistId.collectAsStateWithLifecycle()
        val stateHolder = rememberPlaylistTracksScreenState(playlistId)
        val uiState by stateHolder.uiState.collectAsStateWithLifecycle()
        val currentTrack by viewModel.nowPlayingState.currentTrack.collectAsStateWithLifecycle()

        LaunchedEffect(uiState.playlist, uiState.isLoading) {
            if (!uiState.isLoading) {
                onTitleChanged(uiState.playlist?.name ?: "Playlist")
            }
        }

        var trackWithMenu by remember { mutableStateOf<Track?>(null) }

        EntityScreen(
            isLoading = uiState.isLoading,
            content = {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        EntityHeader(viewModel, EntityType.Playlist)
                    }
                    items(uiState.tracks) { track ->
                        Box {
                            TrackCard(
                                track = track,
                                isCurrentlyPlaying = currentTrack?.mediaId == track.id.toString(),
                                onTrackClicked = { viewModel.playTrack(track, uiState.tracks) },
                                onTrackLongPressed = { trackWithMenu = track },
                            )
                            DropdownMenu(
                                expanded = trackWithMenu?.id == track.id,
                                onDismissRequest = { trackWithMenu = null },
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Remove from playlist") },
                                    onClick = {
                                        playlistId?.let { id ->
                                            viewModel.removeTrackFromPlaylist(id, track.id)
                                        }
                                        trackWithMenu = null
                                    },
                                )
                            }
                        }
                    }
                }
            },
            onPlay = { viewModel.playCurrentEntity(EntityType.Playlist, false) },
            onShuffle = { viewModel.playCurrentEntity(EntityType.Playlist, true) },
        )
    }
}
