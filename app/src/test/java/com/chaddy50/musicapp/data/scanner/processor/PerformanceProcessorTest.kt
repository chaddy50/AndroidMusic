package com.chaddy50.musicapp.data.scanner.processor

import com.chaddy50.musicapp.data.entity.Performance
import com.chaddy50.musicapp.data.repository.IPerformanceRepository
import com.chaddy50.musicapp.data.scanner.util.CursorData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PerformanceProcessorTest {

    private fun cursorData(
        albumName: String? = "Symphony No. 5",
        artistName: String? = "Beethoven",
    ) = CursorData(
        trackId = 1L,
        trackTitle = "Movement 1",
        trackNumber = 1,
        trackDuration = 300000L,
        discNumber = 1,
        genreName = "Symphony",
        artistId = 10L,
        artistName = artistName,
        albumArtistName = "Beethoven",
        albumId = 100L,
        albumName = albumName,
        year = "1808",
    )

    @Test
    fun nonClassicalReturnsNull() = runTest {
        val processor = PerformanceProcessor(FakePerformanceRepository(), FakeArtworkSaver())

        val result = processor.process(
            cursorData = cursorData(),
            isClassical = false,
            trackId = 1L,
            genreId = 5L,
            albumId = 100L,
            artistId = 10L,
            yearResolver = { "1808" },
        )

        assertNull(result)
    }

    @Test
    fun classicalInsertsAndReturnsTriple() = runTest {
        val repo = FakePerformanceRepository(nextInsertId = 42L)
        val artworkSaver = FakeArtworkSaver(artworkPath = "/art/42.jpg")
        val processor = PerformanceProcessor(repo, artworkSaver)

        val result = processor.process(
            cursorData = cursorData(),
            isClassical = true,
            trackId = 1L,
            genreId = 5L,
            albumId = 100L,
            artistId = 10L,
            yearResolver = { "1808" },
        )

        assertEquals(42L, result?.first)
        assertEquals("/art/42.jpg", result?.second)
        assertEquals("1808", result?.third)
        assertEquals(1, repo.insertCount)
    }

    @Test
    fun secondCallReturnsCachedResult() = runTest {
        val repo = FakePerformanceRepository(nextInsertId = 42L)
        val processor = PerformanceProcessor(repo, FakeArtworkSaver())

        processor.process(
            cursorData = cursorData(),
            isClassical = true,
            trackId = 1L,
            genreId = 5L,
            albumId = 100L,
            artistId = 10L,
            yearResolver = { "1808" },
        )

        val result = processor.process(
            cursorData = cursorData(),
            isClassical = true,
            trackId = 2L,
            genreId = 5L,
            albumId = 100L,
            artistId = 10L,
            yearResolver = { "1808" },
        )

        assertEquals(42L, result?.first)
        assertEquals(1, repo.insertCount) // no second insert
    }

    @Test
    fun insertConflictFallsBackToQuery() = runTest {
        val repo = FakePerformanceRepository(nextInsertId = -1L, findResult = 99L)
        val artworkSaver = FakeArtworkSaver(artworkPath = "/art/99.jpg")
        val processor = PerformanceProcessor(repo, artworkSaver)

        val result = processor.process(
            cursorData = cursorData(),
            isClassical = true,
            trackId = 1L,
            genreId = 5L,
            albumId = 100L,
            artistId = 10L,
            yearResolver = { "1808" },
        )

        assertEquals(99L, result?.first)
        assertEquals("/art/99.jpg", result?.second)
    }

    @Test
    fun insertConflictWithNoFallbackReturnsNull() = runTest {
        val repo = FakePerformanceRepository(nextInsertId = -1L, findResult = null)
        val processor = PerformanceProcessor(repo, FakeArtworkSaver())

        val result = processor.process(
            cursorData = cursorData(),
            isClassical = true,
            trackId = 1L,
            genreId = 5L,
            albumId = 100L,
            artistId = 10L,
            yearResolver = { "1808" },
        )

        assertNull(result)
    }

    @Test
    fun nullAlbumNameFallsBackToUnknown() = runTest {
        val repo = FakePerformanceRepository(nextInsertId = 1L)
        val processor = PerformanceProcessor(repo, FakeArtworkSaver())

        processor.process(
            cursorData = cursorData(albumName = null),
            isClassical = true,
            trackId = 1L,
            genreId = 5L,
            albumId = 100L,
            artistId = 10L,
            yearResolver = { "1808" },
        )

        assertEquals("Unknown Album", repo.lastInsertedPerformance?.albumName)
    }

    @Test
    fun nullArtistNameFallsBackToUnknown() = runTest {
        val repo = FakePerformanceRepository(nextInsertId = 1L)
        val processor = PerformanceProcessor(repo, FakeArtworkSaver())

        processor.process(
            cursorData = cursorData(artistName = null),
            isClassical = true,
            trackId = 1L,
            genreId = 5L,
            albumId = 100L,
            artistId = 10L,
            yearResolver = { "1808" },
        )

        assertEquals("Unknown Artist", repo.lastInsertedPerformance?.artistName)
    }
}

private class FakePerformanceRepository(
    private val nextInsertId: Long = 1L,
    private val findResult: Long? = null,
) : IPerformanceRepository {
    var insertCount = 0
    var lastInsertedPerformance: Performance? = null

    override suspend fun insert(performance: Performance): Long {
        insertCount++
        lastInsertedPerformance = performance
        return nextInsertId
    }

    override suspend fun findByAlbumAndArtist(albumId: Long, artistId: Long): Long? = findResult
}