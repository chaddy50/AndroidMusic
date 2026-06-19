package com.chaddy50.froh.data.scanner.processor

import com.chaddy50.froh.data.scanner.util.CursorData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@RunWith(RobolectricTestRunner::class)
class TrackProcessorTest {

    private fun cursorData(
        trackTitle: String? = "Symphony No. 5",
        trackDuration: Long? = 300000L,
        discNumber: Int? = 1,
    ) = CursorData(
        trackId = 1L,
        trackTitle = trackTitle,
        trackNumber = null,
        trackDuration = trackDuration,
        discNumber = discNumber,
        genreName = null,
        artistId = null,
        artistName = null,
        albumArtistName = null,
        albumId = null,
        albumName = null,
        year = null,
        lastModifiedAt = 0
    )

    private fun process(
        cursorData: CursorData = cursorData(),
        trackId: Long = 10L,
        trackNumber: Int = 3,
        genreId: Long = 1L,
        genreName: String = "Classical",
        parentGenreId: Long? = null,
        parentGenreName: String? = null,
        artistId: Long = 20L,
        artistName: String = "Beethoven",
        albumId: Long = 30L,
        albumName: String = "Album",
        albumArtworkPath: String? = "/art/30.jpg",
        albumArtistId: Long = 40L,
        albumArtistName: String = "Beethoven",
        performanceId: Long? = 50L,
        year: String = "1808",
    ) = TrackProcessor().process(
        cursorData, trackId, trackNumber, genreId, genreName,
        parentGenreId, parentGenreName, artistId, artistName,
        albumId, albumName, albumArtworkPath, albumArtistId,
        albumArtistName, performanceId, year,
    )

    @Test
    fun returnsTrackWithCorrectFields() {
        val track = process()

        assertEquals(10L, track.id)
        assertEquals("Symphony No. 5", track.title)
        assertEquals(3, track.number)
        assertEquals(30L, track.albumId)
        assertEquals("Album", track.albumName)
        assertEquals(20L, track.artistId)
        assertEquals("Beethoven", track.artistName)
        assertEquals(40L, track.albumArtistId)
        assertEquals("Beethoven", track.albumArtistName)
        assertEquals(1L, track.genreId)
        assertEquals("Classical", track.genreName)
        assertEquals(50L, track.performanceId)
        assertEquals("/art/30.jpg", track.artworkPath)
        assertEquals("1808", track.year)
    }

    @Test
    fun constructsUriFromTrackId() {
        val track = process(trackId = 42L)

        assertEquals("content://media/external/audio/media/42", track.uri)
    }

    @Test
    fun nullTrackTitleFallsBackToUnknownTitle() {
        val track = process(cursorData = cursorData(trackTitle = null))

        assertEquals("Unknown Title", track.title)
    }

    @Test
    fun nullDiscNumberDefaultsToZero() {
        val track = process(cursorData = cursorData(discNumber = null))

        assertEquals(0, track.discNumber)
    }

    @Test
    fun nullTrackDurationDefaultsToZero() {
        val track = process(cursorData = cursorData(trackDuration = null))

        assertEquals(0L.toDuration(DurationUnit.MILLISECONDS), track.duration)
    }

    @Test
    fun nullArtworkPathPassedThrough() {
        val track = process(albumArtworkPath = null)

        assertNull(track.artworkPath)
    }

    @Test
    fun setsParentGenreFields() {
        val track = process(parentGenreId = 99L, parentGenreName = "Parent Genre")

        assertEquals(99L, track.parentGenreId)
        assertEquals("Parent Genre", track.parentGenreName)
    }

    @Test
    fun nullPerformanceIdPassedThrough() {
        val track = process(performanceId = null)

        assertNull(track.performanceId)
    }
}
