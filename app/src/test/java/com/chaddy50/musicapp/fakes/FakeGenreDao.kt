package com.chaddy50.musicapp.fakes

import com.chaddy50.musicapp.data.dao.GenreDao
import com.chaddy50.musicapp.data.entity.Genre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeGenreDao(
    private val topLevelGenresFlow: MutableStateFlow<List<Genre>> = MutableStateFlow(emptyList()),
    val allGenres: MutableStateFlow<List<Genre>> = MutableStateFlow(emptyList()),
) : GenreDao {
    val genres = mutableMapOf<String, Genre>()
    var nextInsertId = 1L
    var insertCount = 0

    override suspend fun insert(genre: Genre): Long {
        insertCount++
        if (genres.containsKey(genre.name)) return -1L
        val id = nextInsertId++
        genres[genre.name] = genre.copy(id = id)
        return id
    }

    override suspend fun getGenreByName(name: String): Genre? = genres[name]

    override fun getAllTopLevelGenres(): Flow<List<Genre>> = topLevelGenresFlow

    override fun getGenreById(id: Long): Flow<Genre?> =
        allGenres.map { list -> list.find { it.id == id } }

    override fun getGenreName(genreId: Long): Flow<String?> =
        allGenres.map { list -> list.find { it.id == genreId }?.name }

    override fun getSubGenreIds(parentGenreId: Long): List<Long> =
        allGenres.value.filter { it.parentGenreId == parentGenreId }.map { it.id }

    override fun getSubGenres(parentGenreId: Long): Flow<List<Genre>> =
        allGenres.map { list -> list.filter { it.parentGenreId == parentGenreId } }

    override fun getSubGenresForAlbumArtist(parentGenreId: Long, albumArtistId: Long): Flow<List<Genre>> =
        allGenres.map { list -> list.filter { it.parentGenreId == parentGenreId } }

    override fun getParentGenreId(genreId: Long): Flow<Long?> =
        allGenres.map { list -> list.find { it.id == genreId }?.parentGenreId }

    override suspend fun update(genre: Genre) = TODO()
    override suspend fun delete(genre: Genre) = TODO()
    override fun getNumberOfGenres(): Flow<Int> = TODO()
    override fun getAllGenres(): Flow<List<Genre>> = TODO()
    override fun getNumberOfTopLevelGenres(): Flow<Int> = TODO()
    override fun getNumberOfSubGenresForAlbumArtist(parentGenreId: Long, albumArtistId: Long): Flow<Int> = TODO()
}
