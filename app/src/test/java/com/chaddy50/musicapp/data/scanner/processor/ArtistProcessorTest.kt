package com.chaddy50.musicapp.data.scanner.processor

import com.chaddy50.musicapp.data.entity.Artist
import com.chaddy50.musicapp.data.repository.IArtistRepository
import com.chaddy50.musicapp.data.scanner.util.CursorData
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ArtistProcessorTest {

    private fun cursorData(
        artistId: Long? = 10L,
        artistName: String? = "Beethoven",
    ) = CursorData(
        trackId = 1L,
        trackTitle = null,
        trackNumber = null,
        trackDuration = null,
        discNumber = null,
        genreName = null,
        artistId = artistId,
        artistName = artistName,
        albumArtistName = null,
        albumId = null,
        albumName = null,
        year = null,
    )

    @Test
    fun returnsArtistIdAndName() = runTest {
        val repo = FakeArtistRepository()
        val processor = ArtistProcessor(repo)

        val result = processor.process(cursorData())

        assertEquals(10L, result.first)
        assertEquals("Beethoven", result.second)
        assertEquals(1, repo.insertCount)
    }

    @Test
    fun secondCallReturnsCachedResult() = runTest {
        val repo = FakeArtistRepository()
        val processor = ArtistProcessor(repo)

        processor.process(cursorData())
        val result = processor.process(cursorData())

        assertEquals(10L, result.first)
        assertEquals("Beethoven", result.second)
        assertEquals(1, repo.insertCount)
    }

    @Test
    fun differentArtistIdsAreNotCached() = runTest {
        val repo = FakeArtistRepository()
        val processor = ArtistProcessor(repo)

        processor.process(cursorData(artistId = 10L, artistName = "Beethoven"))
        processor.process(cursorData(artistId = 20L, artistName = "Mozart"))

        assertEquals(2, repo.insertCount)
    }

    @Test
    fun nullArtistNameFallsBackToUnknown() = runTest {
        val repo = FakeArtistRepository()
        val processor = ArtistProcessor(repo)

        val result = processor.process(cursorData(artistName = null))

        assertEquals("Unknown Artist", result.second)
    }

    @Test
    fun nullArtistIdFallsBackToNegativeOne() = runTest {
        val repo = FakeArtistRepository()
        val processor = ArtistProcessor(repo)

        val result = processor.process(cursorData(artistId = null))

        assertEquals(-1L, result.first)
    }
}

private class FakeArtistRepository : IArtistRepository {
    var insertCount = 0

    override suspend fun insert(artist: Artist) {
        insertCount++
    }
}
