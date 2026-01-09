package com.chaddy50.musicapp.data.repository

import com.chaddy50.musicapp.data.dao.TrackDao
import com.chaddy50.musicapp.data.entity.Track
import kotlinx.coroutines.flow.Flow

class TrackRepository(private val trackDao: TrackDao) {

    fun getAllTracks(): Flow<List<Track>> = trackDao.getAllTracks()

    fun getTrackById(id: Int): Flow<Track?> = trackDao.getTrackById(id)

    fun getTracksForGenre(genreId: Int) = trackDao.getTracksForGenre(genreId)

    fun getTracksForAlbum(albumId: Int): Flow<List<Track>> = trackDao.getTracksForAlbum(albumId)

    fun getTracksForPerformance(performanceId: Int): Flow<List<Track>> = trackDao.getTracksForPerformance(performanceId)

    fun getTracksForAlbumArtist(albumArtistId: Int) = trackDao.getTracksForAlbumArtist(albumArtistId)

    fun getNumberOfTracksInAlbum(albumId: Int) = trackDao.getNumberOfTracksInAlbum(albumId)

    fun getNumberOfTracks() = trackDao.getNumberOfTracks()

    fun getNumberOfTracksInPerformance(performanceId: Int) = trackDao.getNumberOfTracksInPerformance(performanceId)

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
