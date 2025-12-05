package com.chaddy50.musicapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.chaddy50.musicapp.data.entity.Album
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(album: Album)

    @Update
    suspend fun update(album: Album)

    @Delete
    suspend fun delete(album: Album)

    @Query("SELECT * FROM albums WHERE id = :id")
    fun getAlbumById(id: Int): Flow<Album?>

    @Query("SELECT * FROM albums ORDER BY title ASC")
    fun getAllAlbums(): Flow<List<Album>>

    @Query("SELECT * FROM albums WHERE artistId = :artistId ORDER BY year DESC")
    fun getAlbumsForArtist(artistId: Int): Flow<List<Album>>

    @Query("""
        SELECT DISTINCT albums.* FROM albums 
        INNER JOIN tracks ON tracks.albumId = albums.id
        WHERE albums.artistId = :albumArtistId AND tracks.genreId = :genreId
        ORDER BY albums.year DESC
    """)
    fun getAlbumsForArtistInGenre(albumArtistId: Int, genreId: Int): Flow<List<Album>>

    @Query("SELECT title FROM albums WHERE id = :albumId")
    fun getAlbumName(albumId: Int): Flow<String?>
}
