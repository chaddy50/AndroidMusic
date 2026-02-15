package com.chaddy50.musicapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.chaddy50.musicapp.data.entity.Performance
import kotlinx.coroutines.flow.Flow

@Dao
interface PerformanceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(performance: Performance): Long

    @Update
    suspend fun update(performance: Performance)

    @Delete
    suspend fun delete(performance: Performance)

    @Query("SELECT * FROM performances WHERE id = :id")
    fun getPerformanceById(id: Long): Flow<Performance>

    @Query("SELECT * FROM performances WHERE albumId = :albumId")
    fun getPerformancesForAlbum(albumId: Long): Flow<List<Performance>>

    @Query("SELECT * FROM performances WHERE albumId = :albumId AND genreId = :genreId")
    fun getPerformancesForAlbumForGenre(albumId: Long, genreId: Long): Flow<List<Performance>>

    @Query("SELECT COUNT(*) FROM performances WHERE albumId = :albumId")
    fun getNumberOfPerformancesForAlbum(albumId: Long): Flow<Int>

    @Query("SELECT id FROM performances WHERE albumId = :albumId AND artistId = :artistId LIMIT 1")
    suspend fun findByAlbumAndArtist(albumId: Long, artistId: Long): Long?
}