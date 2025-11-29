package com.chaddy50.musicapp.data.repository

import com.chaddy50.musicapp.data.dao.AlbumArtistDao
import com.chaddy50.musicapp.data.entity.AlbumArtist
import kotlinx.coroutines.flow.Flow

class AlbumArtistRepository(private val albumArtistDao: AlbumArtistDao) {
    fun getAllAlbumArtists(): Flow<List<AlbumArtist>> = albumArtistDao.getAllAlbumArtists()

    fun getAlbumArtistsForGenre(genreId: Int): Flow<List<AlbumArtist>> = albumArtistDao.getAlbumArtistsForGenre(genreId)

    fun getAlbumArtistName(albumArtistId: Int) = albumArtistDao.getAlbumArtistName(albumArtistId)

    fun getAlbumArtistByName(albumArtistName: String) = albumArtistDao.getAlbumArtistByName(albumArtistName)

    suspend fun findOrInsertAlbumArtist(albumArtistName: String, genreId: Int): Int {
        val existingAlbumArtist = albumArtistDao.getAlbumArtistByName(albumArtistName)
        if (existingAlbumArtist != null) return existingAlbumArtist.id

        val newAlbumArtist = AlbumArtist(
            name = albumArtistName,
            genreId = genreId
        )
        albumArtistDao.insert(newAlbumArtist)
        return albumArtistDao.getAlbumArtistByName(albumArtistName)?.id ?: -1
    }

    suspend fun insert(albumArtist: AlbumArtist) {
        albumArtistDao.insert(albumArtist)
    }

    suspend fun update(albumArtist: AlbumArtist) {
        albumArtistDao.update(albumArtist)
    }

    suspend fun delete(albumArtist: AlbumArtist) {
        albumArtistDao.delete(albumArtist)
    }
}
