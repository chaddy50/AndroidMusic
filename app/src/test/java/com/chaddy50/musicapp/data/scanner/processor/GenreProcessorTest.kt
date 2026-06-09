
package com.chaddy50.musicapp.data.scanner.processor

import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.scanner.util.CursorData
import com.chaddy50.musicapp.fakes.FakeGenreDao
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

private fun cursorData(genreName: String? = "Rock") = CursorData(
    trackId = 1L, trackTitle = "Track", trackNumber = 1,
    trackDuration = 200000L, discNumber = 1, genreName = genreName,
    artistId = 1L, artistName = "Artist", albumArtistName = "Artist",
    albumId = 1L, albumName = "Album", year = "2024", lastModifiedAt = 0L,
)

class GenreProcessorProcessTest {

    @Test
    fun returnsGenreIdAndName() = runTest {
        val dao = FakeGenreDao()
        val processor = GenreProcessor(GenreRepository(dao))
        val result = processor.process(cursorData(genreName = "Rock"))
        assertEquals("Rock", result.genreName)
        assertEquals(1L, result.genreId)
    }

    @Test
    fun nullGenreNameDefaultsToUnknownGenre() = runTest {
        val dao = FakeGenreDao()
        val processor = GenreProcessor(GenreRepository(dao))
        val result = processor.process(cursorData(genreName = null))
        assertEquals("Unknown Genre", result.genreName)
    }

    @Test
    fun nonClassicalGenreHasNullParentAndIsNotClassical() = runTest {
        val dao = FakeGenreDao()
        val processor = GenreProcessor(GenreRepository(dao))
        val result = processor.process(cursorData(genreName = "Rock"))
        assertNull(result.parentGenreId)
        assertFalse(result.isClassical)
    }

    @Test
    fun classicalSubGenreHasParentIdAndIsClassical() = runTest {
        val dao = FakeGenreDao()
        val processor = GenreProcessor(GenreRepository(dao))
        processor.setUpClassicalMappings()
        val result = processor.process(cursorData(genreName = "Symphony"))
        assertTrue(result.isClassical)
        assertNotNull(result.parentGenreId)
    }

    @Test
    fun classicalParentGenreIsNotMarkedClassical() = runTest {
        val dao = FakeGenreDao()
        val processor = GenreProcessor(GenreRepository(dao))
        processor.setUpClassicalMappings()
        val result = processor.process(cursorData(genreName = "Classical"))
        assertFalse(result.isClassical)
    }

    @Test
    fun secondCallWithSameGenreReturnsCachedId() = runTest {
        val dao = FakeGenreDao()
        val processor = GenreProcessor(GenreRepository(dao))
        val first = processor.process(cursorData(genreName = "Jazz"))
        val second = processor.process(cursorData(genreName = "Jazz"))
        assertEquals(first.genreId, second.genreId)
        assertEquals(1, dao.insertCount)
    }

    @Test
    fun differentGenresGetDifferentIds() = runTest {
        val dao = FakeGenreDao()
        val processor = GenreProcessor(GenreRepository(dao))
        val rock = processor.process(cursorData(genreName = "Rock"))
        val jazz = processor.process(cursorData(genreName = "Jazz"))
        assertNotEquals(rock.genreId, jazz.genreId)
        assertEquals(2, dao.insertCount)
    }
}

class GenreProcessorSetUpClassicalMappingsTest {

    @Test
    fun insertsClassicalGenreIntoRepository() = runTest {
        val dao = FakeGenreDao()
        val processor = GenreProcessor(GenreRepository(dao))
        processor.setUpClassicalMappings()
        assertNotNull(dao.genres["Classical"])
    }

    @Test
    fun classicalSubGenresMapToClassicalParentAfterSetup() = runTest {
        val dao = FakeGenreDao()
        val processor = GenreProcessor(GenreRepository(dao))
        processor.setUpClassicalMappings()

        val subGenres = listOf(
            "Solo Piano", "Symphony", "String Quartet",
            "Piano Concerto", "Ballet", "Violin Concerto",
        )
        for (name in subGenres) {
            val result = processor.process(cursorData(genreName = name))
            assertTrue("$name should be classical", result.isClassical)
            assertNotNull("$name should have parentGenreId", result.parentGenreId)
        }
    }

    @Test
    fun callingSetupTwiceDoesNotDuplicateClassicalGenre() = runTest {
        val dao = FakeGenreDao()
        val processor = GenreProcessor(GenreRepository(dao))
        processor.setUpClassicalMappings()
        processor.setUpClassicalMappings()
        assertEquals(1, dao.insertCount)
    }
}

class ShouldFetchArtistArtworkForGenreTest {

    private val denyList = listOf("Anime", "Movie", "Video Game")

    @Test
    fun nullReturnsFalse() {
        assertFalse(shouldFetchArtistArtworkForGenre(null, denyList))
    }

    @Test
    fun genreInDenyListReturnsFalse() {
        for (genre in denyList) {
            assertFalse(shouldFetchArtistArtworkForGenre(genre, denyList))
        }
    }

    @Test
    fun genreNotInDenyListReturnsTrue() {
        assertTrue(shouldFetchArtistArtworkForGenre("Classical", denyList))
        assertTrue(shouldFetchArtistArtworkForGenre("Rock", denyList))
    }

    @Test
    fun emptyStringReturnsTrue() {
        assertTrue(shouldFetchArtistArtworkForGenre("", denyList))
    }

    @Test
    fun matchIsCaseSensitive() {
        assertTrue(shouldFetchArtistArtworkForGenre("anime", denyList))
        assertTrue(shouldFetchArtistArtworkForGenre("MOVIE", denyList))
    }

    @Test
    fun emptyDenyListAlwaysReturnsTrue() {
        assertTrue(shouldFetchArtistArtworkForGenre("Anime", emptyList()))
    }
}
