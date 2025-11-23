package com.chaddy50.musicapp.data.repository

import com.chaddy50.musicapp.data.dao.GenreDao
import com.chaddy50.musicapp.data.entity.Genre
import kotlinx.coroutines.flow.Flow

class GenreRepository(private val genreDao: GenreDao) {

    fun getAllGenres(): Flow<List<Genre>> = genreDao.getAllGenres()

    fun getGenreName(genreId: Int) = genreDao.getGenreName(genreId)

    fun getGenreById(id: Int): Flow<Genre?> = genreDao.getGenreById(id)

    suspend fun insert(genre: Genre) {
        genreDao.insert(genre)
    }

    suspend fun update(genre: Genre) {
        genreDao.update(genre)
    }

    suspend fun delete(genre: Genre) {
        genreDao.delete(genre)
    }
}
