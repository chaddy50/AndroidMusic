package com.chaddy50.musicapp.data.repository

import com.chaddy50.musicapp.data.dao.GenreDao
import com.chaddy50.musicapp.data.entity.Genre
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class GenreRepository(private val genreDao: GenreDao) {

    suspend fun insert(genre: Genre) {
        genreDao.insert(genre)
    }

    suspend fun update(genre: Genre) {
        genreDao.update(genre)
    }

    suspend fun delete(genre: Genre) {
        genreDao.delete(genre)
    }

    fun getAllGenres() = genreDao.getAllGenres()

    fun getGenreName(genreId: Int) = genreDao.getGenreName(genreId)

    fun getGenreById(id: Int) = genreDao.getGenreById(id)

    suspend fun getGenreByName(name: String) = genreDao.getGenreByName(name)

    fun getAllTopLevelGenres() = genreDao.getAllTopLevelGenres()

    suspend fun findOrInsertGenreByName(name: String, parentGenreId: Int? = null): Int {
        val existingGenre = genreDao.getGenreByName(name)
        if (existingGenre != null) {
            return existingGenre.id
        }

        val newGenre = Genre(name = name, parentGenreId = parentGenreId)
        val newGenreId = genreDao.insert(newGenre)
        return if (newGenreId != -1L) {
            newGenreId.toInt()
        } else {
            genreDao.getGenreByName(name)!!.id
        }
    }

    fun getSubGenresForAlbumArtist(parentGenreId: Int, albumArtistId: Int): Flow<List<Genre>> {
        return flow {
            emitAll(genreDao.getSubGenresForAlbumArtist(parentGenreId, albumArtistId))
        }.flowOn(Dispatchers.IO)
    }

    fun getSubGenres(parentGenreId: Int): Flow<List<Genre>> {
        return flow {
            emitAll(genreDao.getSubGenres(parentGenreId))
        }.flowOn(Dispatchers.IO)
    }
}
