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

    @Query("SELECT COUNT(*) FROM albums")
    fun getNumberOfAlbums(): Flow<Int>

    @Query("SELECT * FROM albums WHERE id = :id")
    fun getAlbumById(id: Int): Flow<Album?>

    @Query("SELECT * FROM albums ORDER BY title ASC")
    fun getAllAlbums(): Flow<List<Album>>

    @Query("""
        SELECT * FROM albums 
        WHERE artistId = :artistId 
        ORDER BY
            CASE WHEN :shouldSortByCatalogueNumber = 1 THEN albums.catalogueNumber END ASC,
            CASE WHEN :shouldSortByCatalogueNumber = 0 THEN albums.year END DESC
    """)
    fun getAlbumsForArtist(artistId: Int, shouldSortByCatalogueNumber: Boolean): Flow<List<Album>>

    @Query("""
        SELECT DISTINCT albums.* FROM albums 
        INNER JOIN tracks ON tracks.albumId = albums.id
        WHERE albums.artistId = :albumArtistId AND tracks.genreId = :genreId
        ORDER BY 
            CASE WHEN :shouldSortByCatalogueNumber = 1 THEN albums.catalogueNumber END ASC,
            CASE WHEN :shouldSortByCatalogueNumber = 0 THEN albums.year END DESC
    """)
    fun getAlbumsForArtistInGenre(
        albumArtistId: Int,
        genreId: Int,
        shouldSortByCatalogueNumber: Boolean,
    ): Flow<List<Album>>

    @Query("SELECT title FROM albums WHERE id = :albumId")
    fun getAlbumName(albumId: Int): Flow<String?>

    @Query("SELECT COUNT(*) FROM albums WHERE artistId = :albumArtistId")
    fun getNumberOfAlbumsForAlbumArtist(albumArtistId: Int): Flow<Int>

    @Query("""
        SELECT COUNT(DISTINCT(albums.id)) FROM albums
        INNER JOIN tracks ON tracks.albumId = albums.id
        WHERE albums.artistId = :albumArtistId
        AND tracks.genreId = :genreId
    """)
    fun getNumberOfAlbumsForAlbumArtistInGenre(albumArtistId: Int, genreId: Int): Flow<Int>
}
