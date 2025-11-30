package com.chaddy50.musicapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.chaddy50.musicapp.data.entity.AlbumArtist
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumArtistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(albumArtist: AlbumArtist): Long

    @Update
    suspend fun update(albumArtist: AlbumArtist)

    @Delete
    suspend fun delete(albumArtist: AlbumArtist)

    @Query("SELECT * FROM albumArtists WHERE id = :artistId")
    fun getAlbumsForArtist(artistId: Int): Flow<List<AlbumArtist>>

    @Query("SELECT * FROM albumArtists WHERE genreID = :genreId ORDER BY name")
    fun getAlbumArtistsForGenre(genreId: Int): Flow<List<AlbumArtist>>

    @Query("SELECT * FROM albumArtists ORDER BY name ASC")
    fun getAllAlbumArtists(): Flow<List<AlbumArtist>>

    @Query("SELECT * FROM albumArtists WHERE name = :albumArtistName LIMIT 1")
    fun getAlbumArtistByName(albumArtistName: String): AlbumArtist?

    @Query("SELECT * FROM albumArtists WHERE id = :albumArtistId")
    fun getAlbumArtistById(albumArtistId: Int): Flow<AlbumArtist?>

    @Query("SELECT name FROM albumArtists WHERE id = :albumArtistId")
    fun getAlbumArtistName(albumArtistId: Int): Flow<String?>
}
