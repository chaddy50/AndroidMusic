package com.chaddy50.musicapp.data.repository

import com.chaddy50.musicapp.data.dao.AlbumArtistDao
import com.chaddy50.musicapp.data.dao.GenreDao
import com.chaddy50.musicapp.data.entity.AlbumArtist
import com.chaddy50.musicapp.utilities.stripArticles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AlbumArtistRepository(
    private val albumArtistDao: AlbumArtistDao,
    private val genreDao: GenreDao,
) {
    suspend fun insert(albumArtist: AlbumArtist) {
        albumArtistDao.insert(albumArtist)
    }

    suspend fun update(albumArtist: AlbumArtist) {
        albumArtistDao.update(albumArtist)
    }

    suspend fun delete(albumArtist: AlbumArtist) {
        albumArtistDao.delete(albumArtist)
    }

    fun getNumberOfAlbumArtists() = albumArtistDao.getNumberOfAlbumArtists()

    fun getAllAlbumArtists(): Flow<List<AlbumArtist>> = albumArtistDao.getAllAlbumArtists()

    fun getAlbumArtistName(albumArtistId: Int) = albumArtistDao.getAlbumArtistName(albumArtistId)

    fun getAlbumArtistById(albumArtistId: Int) = albumArtistDao.getAlbumArtistById(albumArtistId)

    fun getNumberOfAlbumArtistsForGenre(genreId: Int) = albumArtistDao.getNumberOfAlbumArtistsForGenre(genreId)

    suspend fun findOrInsertAlbumArtist(albumArtistName: String, genreId: Int): Int {
        val existingAlbumArtist = albumArtistDao.getAlbumArtistByName(albumArtistName)
        if (existingAlbumArtist != null) return existingAlbumArtist.id

        val newAlbumArtist = AlbumArtist(
            name = albumArtistName,
            sortName = stripArticles(albumArtistName),
            genreId = genreId,
        )
        albumArtistDao.insert(newAlbumArtist)
        return albumArtistDao.getAlbumArtistByName(albumArtistName)?.id ?: -1
    }

    fun getAlbumArtistsForGenre(genreId: Int): Flow<List<AlbumArtist>> {
        return flow {
            val subGenreIds = genreDao.getSubGenreIds(genreId)
            val genreIds = subGenreIds.ifEmpty { listOf(genreId) }

            emitAll(albumArtistDao.getAlbumArtistsForGenreIds(genreIds))
        }.flowOn(Dispatchers.IO)
    }
}
