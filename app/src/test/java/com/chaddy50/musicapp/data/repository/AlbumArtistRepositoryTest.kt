package com.chaddy50.musicapp.data.repository

import com.chaddy50.musicapp.data.entity.AlbumArtist
import com.chaddy50.musicapp.fakes.FakeAlbumArtistDao
import com.chaddy50.musicapp.fakes.FakeAudioDbRepository
import com.chaddy50.musicapp.fakes.FakeGenreDao
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AlbumArtistRepositoryTest {

    // region findOrInsertAlbumArtist

    @Test
    fun findOrInsertReturnsExistingId() = runTest {
        val dao = FakeAlbumArtistDao()
        val repo = createRepository(dao)

        // First call inserts
        val firstResult = repo.findOrInsertAlbumArtist("The Beatles", genreId = 1)
        assertEquals(1, dao.insertedArtists.size)

        // Second call finds existing, does not insert again
        val secondResult = repo.findOrInsertAlbumArtist("The Beatles", genreId = 1)
        assertEquals(1, dao.insertedArtists.size)
        assertEquals(firstResult, secondResult)
    }

    @Test
    fun findOrInsertInsertsWithStrippedSortName() = runTest {
        val dao = FakeAlbumArtistDao()
        val repo = createRepository(dao)

        repo.findOrInsertAlbumArtist("The Rolling Stones", genreId = 2)

        val inserted = dao.insertedArtists.single()
        assertEquals("The Rolling Stones", inserted.name)
        assertEquals("Rolling Stones", inserted.sortName)
        assertEquals(2L, inserted.genreId)
    }

    @Test
    fun findOrInsertReturnsNegativeOneWhenLookupFails() = runTest {
        val dao = FakeAlbumArtistDao()
        dao.lookupAfterInsertReturnsNull = true
        val repo = createRepository(dao)

        val result = repo.findOrInsertAlbumArtist("Ghost Artist", genreId = 1)

        assertEquals(-1L, result)
    }

    // endregion

    // region fetchAndUpdatePortrait

    @Test
    fun fetchAndUpdatePortraitUpdatesAlbumArtist() = runTest {
        val dao = FakeAlbumArtistDao()
        val audioDbRepo = FakeAudioDbRepository(portraitUrl = "/path/to/portrait.jpg")
        val repo = createRepository(dao, audioDbRepo)
        val artist = AlbumArtist(id = 1, name = "Mozart", sortName = "Mozart", genreId = 1)

        repo.fetchAndUpdatePortrait(artist)

        val updated = dao.updatedArtists.single()
        assertEquals("/path/to/portrait.jpg", updated.portraitPath)
        assertEquals(1L, updated.id)
    }

    @Test
    fun fetchAndUpdatePortraitSetsNullWhenNoResults() = runTest {
        val dao = FakeAlbumArtistDao()
        val audioDbRepo = FakeAudioDbRepository(portraitUrl = null)
        val repo = createRepository(dao, audioDbRepo)
        val artist = AlbumArtist(id = 1, name = "Unknown", sortName = "Unknown", genreId = 1)

        repo.fetchAndUpdatePortrait(artist)

        val updated = dao.updatedArtists.single()
        assertNull(updated.portraitPath)
    }

    // endregion

    private fun createRepository(
        dao: FakeAlbumArtistDao = FakeAlbumArtistDao(),
        audioDbRepo: FakeAudioDbRepository = FakeAudioDbRepository(),
    ): AlbumArtistRepository {
        return AlbumArtistRepository(dao, FakeGenreDao(), audioDbRepo)
    }
}
