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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(albumArtist: AlbumArtist): Long

    @Update
    suspend fun update(albumArtist: AlbumArtist)

    @Delete
    suspend fun delete(albumArtist: AlbumArtist)

    @Query("SELECT COUNT(*) FROM albumArtists")
    fun getNumberOfAlbumArtists(): Flow<Int>

    @Query("SELECT * FROM albumArtists WHERE id = :artistId")
    fun getAlbumsForArtist(artistId: Long): Flow<List<AlbumArtist>>

    @Query("""
        SELECT DISTINCT albumArtists.* FROM albumArtists
        INNER JOIN tracks ON tracks.albumArtistId = albumArtists.id
        WHERE tracks.genreId IN (:genreIds) OR tracks.parentGenreId IN (:genreIds)
        ORDER BY albumArtists.sortName
    """)
    fun getAlbumArtistsForGenreIds(genreIds: List<Long>): Flow<List<AlbumArtist>>

    @Query("SELECT * FROM albumArtists ORDER BY sortName ASC")
    fun getAllAlbumArtists(): Flow<List<AlbumArtist>>

    @Query("SELECT * FROM albumArtists WHERE name = :albumArtistName LIMIT 1")
    fun getAlbumArtistByName(albumArtistName: String): AlbumArtist?

    @Query("SELECT * FROM albumArtists WHERE id = :albumArtistId")
    fun getAlbumArtistById(albumArtistId: Long): Flow<AlbumArtist?>

    @Query("SELECT name FROM albumArtists WHERE id = :albumArtistId")
    fun getAlbumArtistName(albumArtistId: Long): Flow<String?>

    @Query("""
        SELECT COUNT(DISTINCT albumArtists.id) FROM albumArtists
        INNER JOIN tracks ON tracks.albumArtistId = albumArtists.id
        WHERE tracks.genreId = :genreId
        OR tracks.genreId IN (SELECT id FROM genres WHERE parentGenreId = :genreId)
    """)
    fun getNumberOfAlbumArtistsForGenre(genreId: Long): Flow<Int>

    @Query("UPDATE albumArtists SET portraitPath = :portraitPath WHERE id = :id")
    suspend fun updatePortraitPath(id: Long, portraitPath: String?)

    @Query("SELECT * FROM albumArtists WHERE portraitPath IS NULL")
    suspend fun getAlbumArtistsWithoutPortrait(): List<AlbumArtist>

    @Query("""
        SELECT DISTINCT tracks.genreId FROM tracks
        WHERE tracks.albumArtistId = :albumArtistId
    """)
    suspend fun getGenreIdsForAlbumArtist(albumArtistId: Long): List<Long>
}
