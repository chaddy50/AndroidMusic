package com.chaddy50.musicapp.data.scanner.processor

import com.chaddy50.musicapp.data.repository.IComposerRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ComposerProcessorTest {

    @Test
    fun nonClassicalDoesNotFetch() = runTest {
        val repo = FakeComposerRepository()
        val processor = ComposerProcessor(repo)

        processor.process(isClassical = false, albumArtistId = 1L, albumArtistName = "Beethoven")

        assertEquals(0, repo.fetchCount)
    }

    @Test
    fun classicalFetchesComposer() = runTest {
        val repo = FakeComposerRepository()
        val processor = ComposerProcessor(repo)

        processor.process(isClassical = true, albumArtistId = 1L, albumArtistName = "Beethoven")

        assertEquals(1, repo.fetchCount)
        assertEquals("Beethoven", repo.lastFetchedName)
    }

    @Test
    fun sameAlbumArtistIdOnlyFetchedOnce() = runTest {
        val repo = FakeComposerRepository()
        val processor = ComposerProcessor(repo)

        processor.process(isClassical = true, albumArtistId = 1L, albumArtistName = "Beethoven")
        processor.process(isClassical = true, albumArtistId = 1L, albumArtistName = "Beethoven")

        assertEquals(1, repo.fetchCount)
    }

    @Test
    fun differentAlbumArtistIdsFetchSeparately() = runTest {
        val repo = FakeComposerRepository()
        val processor = ComposerProcessor(repo)

        processor.process(isClassical = true, albumArtistId = 1L, albumArtistName = "Beethoven")
        processor.process(isClassical = true, albumArtistId = 2L, albumArtistName = "Mozart")

        assertEquals(2, repo.fetchCount)
    }
}

private class FakeComposerRepository : IComposerRepository {
    var fetchCount = 0
    var lastFetchedName: String? = null

    override suspend fun fetchAndInsertComposer(albumArtistId: Long, albumArtistName: String) {
        fetchCount++
        lastFetchedName = albumArtistName
    }
}
