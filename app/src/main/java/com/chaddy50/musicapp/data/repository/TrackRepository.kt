package com.chaddy50.musicapp.data.repository

import com.chaddy50.musicapp.data.dao.TrackDao
import com.chaddy50.musicapp.data.entity.Track
import kotlinx.coroutines.flow.Flow

class TrackRepository(private val trackDao: TrackDao) {

    fun getAllTracks(): Flow<List<Track>> = trackDao.getAllTracks()

    fun getTracksForGenre(genreId: Long) = trackDao.getTracksForGenre(genreId)

    fun getTracksForAlbum(albumId: Long): Flow<List<Track>> = trackDao.getTracksForAlbum(albumId)

    fun getTracksForPerformance(performanceId: Long): Flow<List<Track>> = trackDao.getTracksForPerformance(performanceId)

    fun getTracksForAlbumArtist(albumArtistId: Long) = trackDao.getTracksForAlbumArtist(albumArtistId)

    fun getTracksForAlbumArtistInGenre(albumArtistId: Long, genreId: Long) = trackDao.getTracksForAlbumArtistInGenre(albumArtistId, genreId)

    fun getNumberOfTracksInAlbum(albumId: Long) = trackDao.getNumberOfTracksInAlbum(albumId)

    fun getNumberOfTracks() = trackDao.getNumberOfTracks()

    fun getNumberOfTracksInPerformance(performanceId: Long) = trackDao.getNumberOfTracksInPerformance(performanceId)

    suspend fun getNumberOfTracksSuspend(): Int {
        return trackDao.getNumberOfTracksSuspend()
    }

    suspend fun insert(track: Track) {
        trackDao.insert(track)
    }

    suspend fun insertMultiple(tracks: List<Track>) {
        trackDao.insertMultiple(tracks)
    }

    suspend fun update(track: Track) {
        trackDao.update(track)
    }

    suspend fun delete(track: Track) {
        trackDao.delete(track)
    }
}
