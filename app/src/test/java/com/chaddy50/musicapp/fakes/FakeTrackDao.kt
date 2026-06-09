package com.chaddy50.musicapp.fakes

import com.chaddy50.musicapp.data.dao.TrackDao
import com.chaddy50.musicapp.data.entity.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeTrackDao : TrackDao {
    val tracks = mutableListOf<Track>()

    override fun getTracksForGenre(genreId: Long): Flow<List<Track>> =
        MutableStateFlow(tracks.filter { it.genreId == genreId || it.parentGenreId == genreId })

    override fun getTracksForAlbum(albumId: Long): Flow<List<Track>> =
        MutableStateFlow(tracks.filter { it.albumId == albumId })

    override fun getTracksForAlbumArtist(albumArtistId: Long): Flow<List<Track>> =
        MutableStateFlow(tracks.filter { it.albumArtistId == albumArtistId })

    override suspend fun insert(track: Track) { tracks.add(track) }
    override suspend fun insertMultiple(tracks: List<Track>) { this.tracks.addAll(tracks) }
    override suspend fun update(track: Track) = TODO()
    override suspend fun delete(track: Track) = TODO()
    override fun getNumberOfTracks(): Flow<Int> = TODO()
    override suspend fun getNumberOfTracksSuspend(): Int = TODO()
    override fun getTrackById(id: Long): Flow<Track?> = TODO()
    override fun getAllTracks(): Flow<List<Track>> = TODO()
    override fun getTracksForPerformance(performanceId: Long): Flow<List<Track>> = TODO()
    override fun getNumberOfTracksInAlbum(albumId: Long): Flow<Int> = TODO()
    override fun getNumberOfTracksInPerformance(performanceId: Long): Flow<Int> = TODO()
    override fun getTracksForAlbumArtistInGenre(albumArtistId: Long, genreId: Long): Flow<List<Track>> = TODO()
    override suspend fun getAllTrackIds(): List<Long> = TODO()
    override suspend fun deleteByIds(ids: List<Long>) = TODO()
}
