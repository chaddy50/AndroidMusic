package com.chaddy50.musicapp.data.repository

import com.chaddy50.musicapp.data.dao.TrackDao
import com.chaddy50.musicapp.data.entity.Track
import kotlinx.coroutines.flow.Flow

class TrackRepository(private val trackDao: TrackDao) {

    fun getAllTracks(): Flow<List<Track>> = trackDao.getAllTracks()

    fun getTrackById(id: Int): Flow<Track?> = trackDao.getTrackById(id)

    fun getTracksForAlbum(albumId: Int): Flow<List<Track>> = trackDao.getTracksForAlbum(albumId)

    suspend fun count(): Int {
        return trackDao.count()
    }

    suspend fun insert(track: Track) {
        trackDao.insert(track)
    }

    suspend fun update(track: Track) {
        trackDao.update(track)
    }

    suspend fun delete(track: Track) {
        trackDao.delete(track)
    }
}
