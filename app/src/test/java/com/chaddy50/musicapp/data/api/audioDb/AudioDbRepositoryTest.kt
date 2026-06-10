package com.chaddy50.musicapp.data.api.audioDb

import com.chaddy50.musicapp.fakes.FakeArtworkDownloader
import com.chaddy50.musicapp.fakes.StubAudioDbService
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.IOException

class AudioDbRepositoryTest {

    @Test
    fun returnsDownloadedPortraitPathWhenApiReturnsResults() = runTest {
        val service = StubAudioDbService(
            response = AudioDbArtistSearchResponse(
                artists = listOf(
                    AudioDbArtist(
                        id = "1", name = "Led Zeppelin",
                        thumbnailUrl = "https://example.com/portrait.jpg",
                        genre = null, country = null, formedYear = null, biography = null,
                    )
                )
            )
        )
        val downloader = FakeArtworkDownloader(resultPath = "/portraits/led_zeppelin.jpg")
        val repo = AudioDbRepository(service, downloader)

        val result = repo.fetchArtistPortraitUrl("Led Zeppelin")

        assertEquals("/portraits/led_zeppelin.jpg", result)
        assertEquals("https://example.com/portrait.jpg", downloader.lastUrl)
    }

    @Test
    fun returnsNullWhenApiReturnsNoResults() = runTest {
        val service = StubAudioDbService(
            response = AudioDbArtistSearchResponse(artists = null)
        )
        val downloader = FakeArtworkDownloader(resultPath = null)
        val repo = AudioDbRepository(service, downloader)

        val result = repo.fetchArtistPortraitUrl("Unknown Artist")

        assertNull(result)
    }

    @Test
    fun returnsNullWhenApiThrowsException() = runTest {
        val service = StubAudioDbService(exception = IOException("Network error"))
        val downloader = FakeArtworkDownloader()
        val repo = AudioDbRepository(service, downloader)

        val result = repo.fetchArtistPortraitUrl("Led Zeppelin")

        assertNull(result)
    }
}
