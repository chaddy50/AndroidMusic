package com.chaddy50.musicapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.chaddy50.musicapp.data.entity.Genre
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(genre: Genre): Long

    @Update
    suspend fun update(genre: Genre)

    @Delete
    suspend fun delete(genre: Genre)

    @Query("SELECT * FROM genres WHERE id = :id")
    fun getGenreById(id: Int): Flow<Genre?>

    @Query("SELECT * FROM genres WHERE name = :name LIMIT 1")
    suspend fun getGenreByName(name: String): Genre?

    @Query("SELECT * FROM genres ORDER BY name ASC")
    fun getAllGenres(): Flow<List<Genre>>

    @Query("SELECT name FROM genres WHERE id = :genreId")
    fun getGenreName(genreId: Int): Flow<String?>

    @Query("SELECT * FROM genres WHERE parentGenreId IS NULL ORDER BY name ASC")
    fun getAllTopLevelGenres(): Flow<List<Genre>>

    @Query("SELECT id FROM genres WHERE parentGenreId = :parentGenreId")
    fun getSubGenreIds(parentGenreId: Int): List<Int>
}
