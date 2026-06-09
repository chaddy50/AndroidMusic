package com.chaddy50.musicapp.fakes

import com.chaddy50.musicapp.data.dao.PlaylistDao
import com.chaddy50.musicapp.data.entity.Playlist
import com.chaddy50.musicapp.data.entity.PlaylistTrack
import com.chaddy50.musicapp.data.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePlaylistDao(
    private val allPlaylistsFlow: MutableStateFlow<List<Playlist>> = MutableStateFlow(emptyList()),
) : PlaylistDao {
    override fun getAllPlaylists(): Flow<List<Playlist>> = allPlaylistsFlow

    override fun getPlaylistById(id: Long): Flow<Playlist?> = TODO()
    override fun getTracksForPlaylist(playlistId: Long): Flow<List<Track>> = TODO()
    override suspend fun getMaxPosition(playlistId: Long): Int? = TODO()
    override suspend fun insertPlaylist(playlist: Playlist): Long = TODO()
    override suspend fun deletePlaylist(playlist: Playlist) = TODO()
    override suspend fun insertPlaylistTrack(playlistTrack: PlaylistTrack) = TODO()
    override suspend fun deletePlaylistTrack(playlistId: Long, trackId: Long) = TODO()
    override fun getPlaylistIdsContainingTrack(trackId: Long): Flow<List<Long>> = TODO()
    override fun getPlaylistIdsContainingAlbum(albumId: Long): Flow<List<Long>> = TODO()
    override fun getPlaylistIdsContainingAlbumArtist(albumArtistId: Long): Flow<List<Long>> = TODO()
    override fun getPlaylistIdsContainingGenre(genreId: Long): Flow<List<Long>> = TODO()
}
