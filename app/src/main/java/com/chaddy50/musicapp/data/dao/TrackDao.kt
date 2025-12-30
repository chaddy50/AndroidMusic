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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMultiple(tracks: List<Track>)

    // Update an existing track.
    @Update
    suspend fun update(track: Track)

    // Delete a track.
    @Delete
    suspend fun delete(track: Track)

    @Query("SELECT COUNT(*) FROM tracks")
    fun getNumberOfTracks(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tracks")
    suspend fun getNumberOfTracksSuspend(): Int

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

    @Query("SELECT * FROM tracks WHERE performanceId = :performanceId ORDER BY discNumber, number ASC")
    fun getTracksForPerformance(performanceId: Int): Flow<List<Track>>

    @Query("SELECT COUNT(*) FROM tracks WHERE albumId = :albumId")
    fun getNumberOfTracksInAlbum(albumId: Int): Flow<Int>

    @Query("SELECT COUNT(*) FROM tracks WHERE performanceId = :performanceId")
    fun getNumberOfTracksInPerformance(performanceId: Int): Flow<Int>
}