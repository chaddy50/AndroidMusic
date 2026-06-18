package com.chaddy50.musicapp.data.repository

import com.chaddy50.musicapp.data.api.openOpus.OpenOpusComposer
import com.chaddy50.musicapp.data.entity.AlbumArtist
import com.chaddy50.musicapp.fakes.FakeAlbumArtistDao
import com.chaddy50.musicapp.fakes.FakeArtworkDownloader
import com.chaddy50.musicapp.fakes.FakeComposerDao
import com.chaddy50.musicapp.fakes.FakeOpenOpusRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ComposerRepositoryTest {

    private val testComposer = OpenOpusComposer(
        id = 100,
        name = "Mozart",
        completeName = "Wolfgang Amadeus Mozart",
        birthDate = "1756-01-27",
        deathDate = "1791-12-05",
        epoch = "Classical",
        portraitUrl = "https://example.com/mozart.jpg",
    )

    private fun createAlbumArtistDao(artists: List<AlbumArtist> = emptyList()): FakeAlbumArtistDao =
        FakeAlbumArtistDao(MutableStateFlow(artists))

    @Test
    fun fetchAndInsertComposerInsertsWithCorrectFields() = runTest {
        val dao = FakeComposerDao()
        val openOpusRepo = FakeOpenOpusRepository(composer = testComposer)
        val artworkDownloader = FakeArtworkDownloader(resultPath = "/portraits/42.jpg")
        val repo = ComposerRepository(dao, openOpusRepo, artworkDownloader, createAlbumArtistDao())

        repo.fetchAndInsertComposer(albumArtistId = 42, albumArtistName = "Mozart")

        val inserted = dao.insertedComposers.single()
        assertEquals(42L, inserted.albumArtistId)
        assertEquals(100L, inserted.openOpusId)
        assertEquals("Wolfgang Amadeus Mozart", inserted.completeName)
        assertEquals("1756", inserted.birthYear)
        assertEquals("1791", inserted.deathYear)
        assertEquals("Classical", inserted.epoch)
        assertEquals("/portraits/42.jpg", inserted.portraitPath)
    }

    @Test
    fun fetchAndInsertComposerDownloadsPortrait() = runTest {
        val artworkDownloader = FakeArtworkDownloader(resultPath = "/portraits/42.jpg")
        val repo = ComposerRepository(
            FakeComposerDao(),
            FakeOpenOpusRepository(composer = testComposer),
            artworkDownloader,
            createAlbumArtistDao(),
        )

        repo.fetchAndInsertComposer(albumArtistId = 42, albumArtistName = "Mozart")

        assertEquals("https://example.com/mozart.jpg", artworkDownloader.lastUrl)
        assertEquals(1, artworkDownloader.downloadCount)
    }

    @Test
    fun fetchAndInsertComposerReturnsEarlyWhenNameEmpty() = runTest {
        val dao = FakeComposerDao()
        val repo = ComposerRepository(
            dao,
            FakeOpenOpusRepository(composer = testComposer),
            FakeArtworkDownloader(),
            createAlbumArtistDao(),
        )

        repo.fetchAndInsertComposer(albumArtistId = 42, albumArtistName = "")

        assertTrue(dao.insertedComposers.isEmpty())
    }

    @Test
    fun fetchAndInsertComposerReturnsEarlyWhenNoResults() = runTest {
        val dao = FakeComposerDao()
        val repo = ComposerRepository(
            dao,
            FakeOpenOpusRepository(composer = null),
            FakeArtworkDownloader(),
            createAlbumArtistDao(),
        )

        repo.fetchAndInsertComposer(albumArtistId = 42, albumArtistName = "Unknown")

        assertTrue(dao.insertedComposers.isEmpty())
    }

    @Test
    fun fetchAndInsertComposerHandlesNullPortraitPath() = runTest {
        val dao = FakeComposerDao()
        val artworkDownloader = FakeArtworkDownloader(resultPath = null)
        val repo = ComposerRepository(
            dao,
            FakeOpenOpusRepository(composer = testComposer),
            artworkDownloader,
            createAlbumArtistDao(),
        )

        repo.fetchAndInsertComposer(albumArtistId = 42, albumArtistName = "Mozart")

        val inserted = dao.insertedComposers.single()
        assertNull(inserted.portraitPath)
    }

    @Test
    fun fetchAndInsertComposerSwallowsExceptions() = runTest {
        val dao = FakeComposerDao()
        val throwingRepo = object : com.chaddy50.musicapp.data.api.openOpus.IOpenOpusRepository {
            override suspend fun findComposerByName(name: String): OpenOpusComposer? {
                throw RuntimeException("Network error")
            }
        }
        val repo = ComposerRepository(dao, throwingRepo, FakeArtworkDownloader(), createAlbumArtistDao())

        repo.fetchAndInsertComposer(albumArtistId = 42, albumArtistName = "Mozart")

        assertTrue(dao.insertedComposers.isEmpty())
    }

    // --- Portrait sync to AlbumArtist ---

    @Test
    fun fetchAndInsertComposerUpdatesAlbumArtistPortraitPath() = runTest {
        val albumArtist = AlbumArtist(id = 42, name = "Mozart", sortName = "Mozart")
        val albumArtistDao = createAlbumArtistDao(listOf(albumArtist))
        val repo = ComposerRepository(
            FakeComposerDao(),
            FakeOpenOpusRepository(composer = testComposer),
            FakeArtworkDownloader(resultPath = "/portraits/42.jpg"),
            albumArtistDao,
        )

        repo.fetchAndInsertComposer(albumArtistId = 42, albumArtistName = "Mozart")

        val updated = albumArtistDao.getAlbumArtistById(42).first()
        assertEquals("/portraits/42.jpg", updated?.portraitPath)
    }

    @Test
    fun fetchAndInsertComposerSetsAlbumArtistPortraitToNullWhenDownloadReturnsNull() = runTest {
        val albumArtist = AlbumArtist(id = 42, name = "Mozart", sortName = "Mozart")
        val albumArtistDao = createAlbumArtistDao(listOf(albumArtist))
        val repo = ComposerRepository(
            FakeComposerDao(),
            FakeOpenOpusRepository(composer = testComposer),
            FakeArtworkDownloader(resultPath = null),
            albumArtistDao,
        )

        repo.fetchAndInsertComposer(albumArtistId = 42, albumArtistName = "Mozart")

        val updated = albumArtistDao.getAlbumArtistById(42).first()
        assertNull(updated?.portraitPath)
    }

    @Test
    fun fetchAndInsertComposerDoesNotUpdateAlbumArtistWhenNameEmpty() = runTest {
        val albumArtist = AlbumArtist(id = 42, name = "Mozart", sortName = "Mozart")
        val albumArtistDao = createAlbumArtistDao(listOf(albumArtist))
        val repo = ComposerRepository(
            FakeComposerDao(),
            FakeOpenOpusRepository(composer = testComposer),
            FakeArtworkDownloader(resultPath = "/portraits/42.jpg"),
            albumArtistDao,
        )

        repo.fetchAndInsertComposer(albumArtistId = 42, albumArtistName = "")

        val unchanged = albumArtistDao.getAlbumArtistById(42).first()
        assertNull(unchanged?.portraitPath)
    }

    @Test
    fun fetchAndInsertComposerDoesNotUpdateAlbumArtistWhenNoResults() = runTest {
        val albumArtist = AlbumArtist(id = 42, name = "Unknown", sortName = "Unknown")
        val albumArtistDao = createAlbumArtistDao(listOf(albumArtist))
        val repo = ComposerRepository(
            FakeComposerDao(),
            FakeOpenOpusRepository(composer = null),
            FakeArtworkDownloader(resultPath = "/portraits/42.jpg"),
            albumArtistDao,
        )

        repo.fetchAndInsertComposer(albumArtistId = 42, albumArtistName = "Unknown")

        val unchanged = albumArtistDao.getAlbumArtistById(42).first()
        assertNull(unchanged?.portraitPath)
    }
}
