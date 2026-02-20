package com.chaddy50.musicapp.ui.screens.playlistsScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.chaddy50.musicapp.data.entity.Playlist
import com.chaddy50.musicapp.ui.composables.CreateNewPlaylistDialog
import com.chaddy50.musicapp.ui.composables.EntityCard
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.ui.screens.MusicAppScreen
import com.chaddy50.musicapp.ui.screens.playlistTracksScreen.PlaylistTracksScreen
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

object PlaylistsScreen : MusicAppScreen {
    override val route = "playlists_screen"

    @Composable
    override fun Content(
        viewModel: MusicAppViewModel,
        navController: NavController,
        backStackEntry: NavBackStackEntry,
        onTitleChanged: (title: String) -> Unit,
    ) {
        val stateHolder = rememberPlaylistsScreenState()
        val uiState by stateHolder.uiState.collectAsStateWithLifecycle()

        var showCreateDialog by remember { mutableStateOf(false) }
        var playlistToDelete by remember { mutableStateOf<Playlist?>(null) }

        EntityScreen(
            isLoading = uiState.isLoading,
            content = {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(uiState.playlists) { playlist ->
                            EntityCard(
                                title = playlist.name,
                                onClick = {
                                    viewModel.updateSelectedPlaylist(playlist.id)
                                    navController.navigate(PlaylistTracksScreen.route)
                                },
                                onLongClick = {
                                    playlistToDelete = playlist
                                },
                            )
                        }
                    }
                    FloatingActionButton(
                        onClick = { showCreateDialog = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = "Create playlist")
                    }
                }
            },
        )

        if (showCreateDialog) {
            CreateNewPlaylistDialog(
                onConfirm = { name ->
                    if (name.isNotBlank()) {
                        viewModel.createPlaylist(name)
                    }
                    showCreateDialog = false
                },
                onDismiss = { showCreateDialog = false },
            )
        }

        playlistToDelete?.let { playlist ->
            AlertDialog(
                onDismissRequest = { playlistToDelete = null },
                title = { Text("Delete playlist") },
                text = { Text("Delete \"${playlist.name}\"?") },
                confirmButton = {
                    TextButton(onClick = {
                        viewModel.deletePlaylist(playlist)
                        playlistToDelete = null
                    }) { Text("Delete") }
                },
                dismissButton = {
                    TextButton(onClick = { playlistToDelete = null }) { Text("Cancel") }
                },
            )
        }
    }
}
