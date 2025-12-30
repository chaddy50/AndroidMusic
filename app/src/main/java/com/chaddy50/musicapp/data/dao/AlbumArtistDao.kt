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

    @Query("SELECT COUNT(*) FROM albumArtists")
    fun getNumberOfAlbumArtists(): Flow<Int>

    @Query("SELECT * FROM albumArtists WHERE id = :artistId")
    fun getAlbumsForArtist(artistId: Int): Flow<List<AlbumArtist>>

    @Query("SELECT * FROM albumArtists WHERE genreID IN (:genreIds) ORDER BY sortName")
    fun getAlbumArtistsForGenreIds(genreIds: List<Int>): Flow<List<AlbumArtist>>

    @Query("SELECT * FROM albumArtists ORDER BY sortName ASC")
    fun getAllAlbumArtists(): Flow<List<AlbumArtist>>

    @Query("SELECT * FROM albumArtists WHERE name = :albumArtistName LIMIT 1")
    fun getAlbumArtistByName(albumArtistName: String): AlbumArtist?

    @Query("SELECT * FROM albumArtists WHERE id = :albumArtistId")
    fun getAlbumArtistById(albumArtistId: Int): Flow<AlbumArtist?>

    @Query("SELECT name FROM albumArtists WHERE id = :albumArtistId")
    fun getAlbumArtistName(albumArtistId: Int): Flow<String?>

    @Query("""
        SELECT COUNT(*) FROM albumartists 
        WHERE genreId = :genreId
        OR genreId IN (SELECT id FROM genres WHERE parentGenreId = :genreId)
    """)
    fun getNumberOfAlbumArtistsForGenre(genreId: Int): Flow<Int>
}
