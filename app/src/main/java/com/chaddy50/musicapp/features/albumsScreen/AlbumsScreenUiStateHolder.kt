package com.chaddy50.musicapp.features.albumsScreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.chaddy50.musicapp.MusicApplication
import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.AlbumRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@Stable
class AlbumsScreenUiStateHolder(
    artistId: Int,
    albumRepository: AlbumRepository,
    albumArtistRepository: AlbumArtistRepository,
    coroutineScope: CoroutineScope,
) {
    lateinit var uiState: StateFlow<AlbumsScreenUiState>

    init {
        var albums: Flow<List<Album>>
        if (artistId != 0) {
            albums = albumRepository.getAlbumsForArtist(artistId)
        } else {
            albums = albumRepository.getAllAlbums()
        }

        var artistName: Flow<String?> = flowOf(null)
        if (artistId != 0) {
            artistName = albumArtistRepository.getAlbumArtistName(artistId)
        }

        uiState = combine(albums, artistName) { albums, artistName ->
            AlbumsScreenUiState(
                artistName ?: "Albums",
                albums,
                false,
            )
        }.stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5_000),
            AlbumsScreenUiState(isLoading = true)
        )
    }
}

@Composable
fun rememberAlbumsScreenState(
    artistId: Int,
    app: MusicApplication = LocalContext.current.applicationContext as MusicApplication,
    albumRepository: AlbumRepository = app.albumRepository,
    albumArtistRepository: AlbumArtistRepository = app.albumArtistRepository,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): AlbumsScreenUiStateHolder {
    return remember(artistId, albumRepository, albumArtistRepository, coroutineScope) {
        AlbumsScreenUiStateHolder(
            artistId,
            albumRepository,
            albumArtistRepository,
            coroutineScope,
        )
    }
}