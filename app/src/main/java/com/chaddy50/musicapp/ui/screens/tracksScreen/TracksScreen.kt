package com.chaddy50.musicapp.ui.screens.tracksScreen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chaddy50.musicapp.MusicApplication
import com.chaddy50.musicapp.data.entity.Track
import com.chaddy50.musicapp.ui.composables.AddToPlaylistSheet
import com.chaddy50.musicapp.ui.composables.EntityScreen
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityHeader
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
import com.chaddy50.musicapp.ui.composables.nowPlayingBar.PlaybackViewModel
import com.chaddy50.musicapp.ui.screens.playlistsScreen.PlaylistViewModel
import kotlinx.coroutines.flow.flowOf

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TracksScreen(
    genreId: Long,
    albumId: Long,
    performanceId: Long?,
    playbackViewModel: PlaybackViewModel,
    playlistViewModel: PlaylistViewModel,
    onTitleChanged: (String) -> Unit,
    screenViewModel: TracksScreenViewModel = hiltViewModel(),
) {
    val app = LocalContext.current.applicationContext as MusicApplication
    val currentTrack by playbackViewModel.nowPlayingState.currentTrack.collectAsStateWithLifecycle()
    val allPlaylists by playlistViewModel.allPlaylists.collectAsStateWithLifecycle()
    val uiState by screenViewModel.uiState.collectAsStateWithLifecycle()

    var trackToAddToPlaylist by remember { mutableStateOf<Track?>(null) }
    val playlistsThatTrackIsAlreadyIn by remember(trackToAddToPlaylist?.id) {
        trackToAddToPlaylist?.let { playlistViewModel.getPlaylistsThatTrackIsAlreadyIn(it.id) } ?: flowOf(emptySet())
    }.collectAsStateWithLifecycle(emptySet())

    LaunchedEffect(uiState.screenTitle, uiState.isLoading) {
        if (!uiState.isLoading) {
            onTitleChanged(uiState.screenTitle)
        }
    }

    val groupedTracks = uiState.tracks.groupBy { it.discNumber }
    val doesAlbumHaveMultipleDiscs = groupedTracks.size > 1

    EntityScreen(
        uiState.isLoading,
        {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    EntityHeader(
                        type = EntityType.Album,
                        genreId = genreId,
                        albumId = albumId,
                        performanceId = performanceId,
                        classicalGenreId = app.classicalGenreId,
                        allPlaylists = allPlaylists,
                        onAddToPlaylist = { playlistId -> playlistViewModel.addAlbumToPlaylist(playlistId, albumId) },
                        onCreateAndAdd = { name -> playlistViewModel.createPlaylistAndAddAlbum(name, albumId) },
                    )
                }

                groupedTracks.forEach { (discNumber, tracks) ->
                    if (doesAlbumHaveMultipleDiscs && (discNumber > 0)) {
                        stickyHeader {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = "Disc $discNumber",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                    items(tracks) { track ->
                        TrackCard(
                            track,
                            currentTrack?.mediaId == track.id.toString(),
                            { playbackViewModel.playTrack(track, uiState.tracks) },
                            onTrackLongPressed = { trackToAddToPlaylist = it },
                        )
                    }
                 }
            }
        },
        onPlay = { playbackViewModel.playTracksForAlbum(albumId, performanceId, false) },
        onShuffle = { playbackViewModel.playTracksForAlbum(albumId, performanceId, true) },
    )

    trackToAddToPlaylist?.let { track ->
        AddToPlaylistSheet(
            allPlaylists = allPlaylists,
            playlistsThatEntityIsAlreadyIn = playlistsThatTrackIsAlreadyIn,
            onAddToPlaylist = { playlistId -> playlistViewModel.addTrackToPlaylist(playlistId, track) },
            onCreateAndAdd = { name -> playlistViewModel.createPlaylistAndAddTrack(name, track) },
            onDismiss = { trackToAddToPlaylist = null },
        )
    }
}
