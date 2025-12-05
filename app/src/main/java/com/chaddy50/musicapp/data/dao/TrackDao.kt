package com.chaddy50.musicapp.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.chaddy50.musicapp.data.entity.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    // Insert a single track. If a track with the same primary key already exists, replace it.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(track: Track)

    // Update an existing track.
    @Update
    suspend fun update(track: Track)

    // Delete a track.
    @Delete
    suspend fun delete(track: Track)

    @Query("SELECT COUNT(*) FROM tracks")
    suspend fun count(): Int

    // Get a single track by its ID. Returns a Flow, which will automatically update
    // if the track's data changes.
    @Query("SELECT * FROM tracks WHERE id = :id")
    fun getTrackById(id: Int): Flow<Track?>

    // Get all tracks from the table, ordered by track number.
    // The Flow will emit a new list of tracks whenever the table's content changes.
    @Query("SELECT * FROM tracks ORDER BY number ASC")
    fun getAllTracks(): Flow<List<Track>>

    // Get all tracks belonging to a specific album.
    @Query("SELECT * FROM tracks WHERE albumID = :albumId ORDER BY discNumber, number ASC")
    fun getTracksForAlbum(albumId: Int): Flow<List<Track>>
}