package com.chaddy50.musicapp.data.scanner.processor

import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.data.repository.IAlbumRepository
import com.chaddy50.musicapp.data.scanner.util.CursorData
import com.chaddy50.musicapp.data.scanner.util.IArtworkSaver
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AlbumProcessorTest {

    private fun cursorData(
        albumId: Long? = 100L,
        albumName: String? = "Symphony No. 5 Op. 67",
    ) = CursorData(
        trackId = 1L,
        trackTitle = null,
        trackNumber = null,
        trackDuration = null,
        discNumber = null,
        genreName = null,
        artistId = null,
        artistName = null,
        albumArtistName = null,
        albumId = albumId,
        albumName = albumName,
        year = null,
        0
    )

    @Test
    fun returnsAlbumProcessorResult() = runTest {
        val repo = FakeAlbumRepository()
        val artworkSaver = FakeArtworkSaver(artworkPath = "/art/100.jpg")
        val processor = AlbumProcessor(repo, artworkSaver)

        val result = processor.process(cursorData(), trackId = 1L, albumArtistId = 5L) { "1808" }

        assertEquals(100L, result.albumId)
        assertEquals("Symphony No. 5 Op. 67", result.albumName)
        assertEquals("/art/100.jpg", result.artworkPath)
        assertEquals("1808", result.year)
    }

    @Test
    fun insertsAlbumWithCorrectFields() = runTest {
        val repo = FakeAlbumRepository()
        val processor = AlbumProcessor(repo, FakeArtworkSaver())

        processor.process(cursorData(), trackId = 1L, albumArtistId = 5L) { "1808" }

        val album = repo.lastInsertedAlbum!!
        assertEquals(100L, album.id)
        assertEquals("Symphony No. 5 Op. 67", album.title)
        assertEquals(67, album.catalogueNumber)
        assertEquals(5L, album.artistId)
        assertEquals("1808", album.year)
    }

    @Test
    fun secondCallReturnsCachedResult() = runTest {
        val repo = FakeAlbumRepository()
        val processor = AlbumProcessor(repo, FakeArtworkSaver())

        processor.process(cursorData(), trackId = 1L, albumArtistId = 5L) { "1808" }
        val result = processor.process(cursorData(), trackId = 2L, albumArtistId = 5L) { "1808" }

        assertEquals(100L, result.albumId)
        assertEquals(1, repo.insertCount)
    }

    @Test
    fun differentAlbumIdsAreNotCached() = runTest {
        val repo = FakeAlbumRepository()
        val processor = AlbumProcessor(repo, FakeArtworkSaver())

        processor.process(cursorData(albumId = 100L), trackId = 1L, albumArtistId = 5L) { "1808" }
        processor.process(cursorData(albumId = 200L), trackId = 2L, albumArtistId = 5L) { "1900" }

        assertEquals(2, repo.insertCount)
    }

    @Test
    fun nullAlbumNameFallsBackToUnknown() = runTest {
        val repo = FakeAlbumRepository()
        val processor = AlbumProcessor(repo, FakeArtworkSaver())

        val result = processor.process(
            cursorData(albumName = null), trackId = 1L, albumArtistId = 5L
        ) { "1808" }

        assertEquals("Unknown Album", result.albumName)
    }

    @Test
    fun nullAlbumIdFallsBackToNegativeOne() = runTest {
        val repo = FakeAlbumRepository()
        val processor = AlbumProcessor(repo, FakeArtworkSaver())

        val result = processor.process(
            cursorData(albumId = null), trackId = 1L, albumArtistId = 5L
        ) { "1808" }

        assertEquals(-1L, result.albumId)
    }

    @Test
    fun albumWithNoCatalogueNumberGetsDefault() = runTest {
        val repo = FakeAlbumRepository()
        val processor = AlbumProcessor(repo, FakeArtworkSaver())

        processor.process(
            cursorData(albumName = "Abbey Road"), trackId = 1L, albumArtistId = 5L
        ) { "1969" }

        assertEquals(99999, repo.lastInsertedAlbum?.catalogueNumber)
    }

    @Test
    fun nullArtworkPathPassedThrough() = runTest {
        val repo = FakeAlbumRepository()
        val processor = AlbumProcessor(repo, FakeArtworkSaver(artworkPath = null))

        val result = processor.process(cursorData(), trackId = 1L, albumArtistId = 5L) { "1808" }

        assertNull(result.artworkPath)
    }
}

class ExtractCatalogNumberTest {
    // --- Opus ---
    @Test
    fun opWithDotAndSpace() {
        assertEquals(118, extractCatalogNumber("Piano Pieces Op. 118"))
    }

    @Test
    fun opWithDotNoSpace() {
        assertEquals(27, extractCatalogNumber("Piano Concerto Op.27"))
    }

    @Test
    fun opNoDot() {
        assertEquals(67, extractCatalogNumber("Symphony Op 67"))
    }

    @Test
    fun opCaseInsensitive() {
        assertEquals(10, extractCatalogNumber("Sonata op. 10"))
    }

    // --- Köchel (Mozart) ---

    @Test
    fun kWithDot() {
        assertEquals(545, extractCatalogNumber("Piano Sonata K. 545"))
    }

    @Test
    fun kNoDot() {
        assertEquals(466, extractCatalogNumber("Piano Concerto K 466"))
    }

    // --- Bach ---

    @Test
    fun bwv() {
        assertEquals(1041, extractCatalogNumber("Violin Concerto BWV 1041"))
    }

    @Test
    fun bwvCaseInsensitive() {
        assertEquals(565, extractCatalogNumber("Toccata and Fugue bwv 565"))
    }

    // --- Hoboken (Haydn) ---

    @Test
    fun hobWithDot() {
        assertEquals(94, extractCatalogNumber("Symphony Hob. 94"))
    }

    @Test
    fun hobNoDot() {
        assertEquals(104, extractCatalogNumber("Symphony Hob 104"))
    }

    // --- Ryom (Vivaldi) ---

    @Test
    fun rv() {
        assertEquals(269, extractCatalogNumber("The Four Seasons RV 269"))
    }

    // --- Deutsch (Schubert) ---

    @Test
    fun dWithDot() {
        assertEquals(944, extractCatalogNumber("Symphony D. 944"))
    }

    @Test
    fun dNoDot() {
        assertEquals(810, extractCatalogNumber("String Quartet D 810"))
    }

    // --- S (Liszt) ---

    @Test
    fun sWithDot() {
        assertEquals(139, extractCatalogNumber("Hungarian Rhapsody S. 139"))
    }

    // --- M (Ravel) ---

    @Test
    fun mWithDot() {
        assertEquals(77, extractCatalogNumber("Daphnis et Chloé M. 77"))
    }

    // --- L (Debussy) ---

    @Test
    fun lWithDot() {
        assertEquals(75, extractCatalogNumber("Prélude à l'après-midi d'un faune L. 75"))
    }

    // --- No match ---

    @Test
    fun noMatchReturnsDefault() {
        assertEquals(99999, extractCatalogNumber("Symphony No. 5"))
    }

    @Test
    fun emptyStringReturnsDefault() {
        assertEquals(99999, extractCatalogNumber(""))
    }

    @Test
    fun plainAlbumNameReturnsDefault() {
        assertEquals(99999, extractCatalogNumber("Abbey Road"))
    }

    // --- Edge cases ---

    @Test
    fun firstMatchWins() {
        assertEquals(10, extractCatalogNumber("Sonata Op. 10 BWV 999"))
    }

    @Test
    fun catalogNumberAtEndOfString() {
        assertEquals(331, extractCatalogNumber("K. 331"))
    }

    @Test
    fun noSpaceBetweenPrefixAndNumber() {
        assertEquals(27, extractCatalogNumber("Op.27"))
    }
}

private class FakeAlbumRepository : IAlbumRepository {
    var insertCount = 0
    var lastInsertedAlbum: Album? = null

    override suspend fun insert(album: Album) {
        insertCount++
        lastInsertedAlbum = album
    }
}

class FakeArtworkSaver(
    private val artworkPath: String? = null,
) : IArtworkSaver {
    override fun loadAndSaveArtwork(trackId: Long, entityId: Long): String? = artworkPath
}
