package com.chaddy50.musicapp.data.scanner.processor

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

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
