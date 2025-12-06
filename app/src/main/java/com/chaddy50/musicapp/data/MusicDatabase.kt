package com.chaddy50.musicapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.chaddy50.musicapp.data.dao.AlbumArtistDao
import com.chaddy50.musicapp.data.dao.AlbumDao
import com.chaddy50.musicapp.data.dao.ArtistDao
import com.chaddy50.musicapp.data.dao.GenreDao
import com.chaddy50.musicapp.data.dao.GenreMappingDao
import com.chaddy50.musicapp.data.dao.PerformanceDao
import com.chaddy50.musicapp.data.entity.Track
import com.chaddy50.musicapp.data.dao.TrackDao
import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.data.entity.AlbumArtist
import com.chaddy50.musicapp.data.entity.Artist
import com.chaddy50.musicapp.data.entity.Genre
import com.chaddy50.musicapp.data.entity.GenreMapping
import com.chaddy50.musicapp.data.entity.Performance
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds


@Database(
    entities = [
        Track::class,
        Album::class,
        Artist::class,
        AlbumArtist::class,
        Genre::class,
        GenreMapping::class,
        Performance::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun albumDao(): AlbumDao
    abstract fun artistDao(): ArtistDao
    abstract fun albumArtistDao(): AlbumArtistDao
    abstract fun genreDao(): GenreDao
    abstract fun genreMappingDao(): GenreMappingDao
    abstract fun performanceDao(): PerformanceDao

    companion object {
        @Volatile
        private var INSTANCE: MusicDatabase? = null

        fun getDatabase(context: Context): MusicDatabase {
            // If the INSTANCE is not null, then return it.
            // If it is, then create the database.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MusicDatabase::class.java,
                    "music.db" // The name of the actual database file.
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

class Converters {    /**
 * Converts a Long (milliseconds) from the database into a Duration object.
 */
@TypeConverter
fun fromTimestamp(value: Long?): Duration? {
    // If the value from the database is null, return null. Otherwise, convert it.
    return value?.milliseconds
}

    /**
     * Converts a Duration object into a Long (milliseconds) to be stored in the database.
     */
    @TypeConverter
    fun durationToTimestamp(duration: Duration?): Long? {
        // If the duration is null, store null. Otherwise, store its value in milliseconds.
        return duration?.inWholeMilliseconds
    }
}