package com.chaddy50.musicapp.fakes

import com.chaddy50.musicapp.data.dao.GenreDao
import com.chaddy50.musicapp.data.entity.Genre
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeGenreDao(
    private val topLevelGenresFlow: MutableStateFlow<List<Genre>> = MutableStateFlow(emptyList()),
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

    override suspend fun update(genre: Genre) = TODO()
    override suspend fun delete(genre: Genre) = TODO()
    override fun getNumberOfGenres(): Flow<Int> = TODO()
    override fun getGenreById(id: Long): Flow<Genre?> = TODO()
    override fun getAllGenres(): Flow<List<Genre>> = TODO()
    override fun getGenreName(genreId: Long): Flow<String?> = TODO()
    override fun getNumberOfTopLevelGenres(): Flow<Int> = TODO()
    override fun getSubGenres(parentGenreId: Long): Flow<List<Genre>> = TODO()
    override fun getSubGenreIds(parentGenreId: Long): List<Long> = TODO()
    override fun getParentGenreId(genreId: Long): Flow<Long?> = TODO()
    override fun getSubGenresForAlbumArtist(parentGenreId: Long, albumArtistId: Long): Flow<List<Genre>> = TODO()
    override fun getNumberOfSubGenresForAlbumArtist(parentGenreId: Long, albumArtistId: Long): Flow<Int> = TODO()
}
