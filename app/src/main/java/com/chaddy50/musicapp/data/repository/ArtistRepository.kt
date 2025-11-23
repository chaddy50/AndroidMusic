package com.chaddy50.musicapp.data.repository

import com.chaddy50.musicapp.data.dao.ArtistDao
import com.chaddy50.musicapp.data.entity.Artist
import kotlinx.coroutines.flow.Flow

class ArtistRepository(private val artistDao: ArtistDao) {

    fun getAllArtists(): Flow<List<Artist>> = artistDao.getAllArtists()

    fun getArtistById(id: Int): Flow<Artist?> = artistDao.getArtistById(id)

    suspend fun insert(artist: Artist) {
        artistDao.insert(artist)
    }

    suspend fun update(artist: Artist) {
        artistDao.update(artist)
    }

    suspend fun delete(artist: Artist) {
        artistDao.delete(artist)
    }
}
