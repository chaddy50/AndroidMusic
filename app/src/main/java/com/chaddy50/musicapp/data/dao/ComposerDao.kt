package com.chaddy50.musicapp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chaddy50.musicapp.data.entity.Composer
import kotlinx.coroutines.flow.Flow

@Dao
interface ComposerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(composer: Composer): Long

    @Query("SELECT * FROM composers WHERE albumArtistId = :albumArtistId")
    fun getComposerForAlbumArtist(albumArtistId: Int): Flow<Composer?>
}
