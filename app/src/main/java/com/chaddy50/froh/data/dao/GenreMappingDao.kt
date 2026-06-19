package com.chaddy50.froh.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chaddy50.froh.data.entity.GenreMapping

@Dao
interface GenreMappingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mappings: List<GenreMapping>)

    @Query("SELECT * FROM genre_mappings")
    suspend fun getAllGenreMappings(): List<GenreMapping>

    @Query("SELECT subGenreName FROM genre_mappings WHERE parentGenreName = 'Classical'")
    suspend fun getClassicalGenreNames(): List<String>

    @Query("DELETE FROM genre_mappings WHERE parentGenreName = 'Classical'")
    suspend fun deleteAllClassicalMappings()
}