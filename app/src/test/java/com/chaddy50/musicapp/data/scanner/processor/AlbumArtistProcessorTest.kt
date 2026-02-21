package com.chaddy50.musicapp.data.scanner.processor

import com.chaddy50.musicapp.data.repository.IAlbumArtistRepository
import com.chaddy50.musicapp.data.scanner.util.CursorData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AlbumArtistProcessorTest {

    private fun cursorData(
        albumArtistName: String? = "Beethoven",
    ) = CursorData(
        trackId = 1L,
        trackTitle = null,
        trackNumber = null,
        trackDuration = null,
        discNumber = null,
        genreName = null,
        artistId = null,
        artistName = null,
        albumArtistName = albumArtistName,
        albumId = null,
        albumName = null,
        year = null,
        0
    )

    @Test
    fun returnsAlbumArtistIdAndName() = runTest {
        val repo = FakeAlbumArtistRepository(nextId = 42L)
        val processor = AlbumArtistProcessor(repo)

        val result = processor.process(cursorData(), genreId = 5L)

        assertEquals(42L, result.first)
        assertEquals("Beethoven", result.second)
        assertEquals(1, repo.insertCount)
    }

    @Test
    fun secondCallWithSameNameReturnsCached() = runTest {
        val repo = FakeAlbumArtistRepository(nextId = 42L)
        val processor = AlbumArtistProcessor(repo)

        processor.process(cursorData(), genreId = 5L)
        val result = processor.process(cursorData(), genreId = 5L)

        assertEquals(42L, result.first)
        assertEquals(1, repo.insertCount)
    }

    @Test
    fun differentNamesAreNotCached() = runTest {
        val repo = FakeAlbumArtistRepository(nextId = 42L)
        val processor = AlbumArtistProcessor(repo)

        processor.process(cursorData(albumArtistName = "Beethoven"), genreId = 5L)
        processor.process(cursorData(albumArtistName = "Mozart"), genreId = 5L)

        assertEquals(2, repo.insertCount)
    }

    @Test
    fun nullAlbumArtistNameFallsBackToUnknown() = runTest {
        val repo = FakeAlbumArtistRepository(nextId = 1L)
        val processor = AlbumArtistProcessor(repo)

        val result = processor.process(cursorData(albumArtistName = null), genreId = 5L)

        assertEquals("Unknown Artist", result.second)
    }
}

private class FakeAlbumArtistRepository(
    private val nextId: Long = 1L,
) : IAlbumArtistRepository {
    var insertCount = 0

    override suspend fun findOrInsertAlbumArtist(albumArtistName: String, genreId: Long): Long {
        insertCount++
        return nextId
    }
}
