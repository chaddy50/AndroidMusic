package com.chaddy50.musicapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chaddy50.musicapp.data.entity.GenreMapping

@Dao
interface GenreMappingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(mappings: List<GenreMapping>)

    @Query("SELECT * FROM genre_mappings")
    suspend fun getAllGenreMappings(): List<GenreMapping>
}