package com.chaddy50.froh.data.scrobbling

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScrobbleManagerTest {

    private lateinit var fakeService: FakeScrobbleService
    private lateinit var scrobbleManager: ScrobbleManager
    private lateinit var testScope: TestScope

    @Before
    fun setup() {
        fakeService = FakeScrobbleService()
        testScope = TestScope()
        scrobbleManager = ScrobbleManager(listOf(fakeService))
        scrobbleManager.scope = testScope
    }

    // --- Now playing ---

    @Test
    fun playbackStartedSubmitsNowPlaying() = testScope.runTest {
        scrobbleManager.onPlaybackStarted("Artist", "Track", "Album", 1, 200_000)
        advanceUntilIdle()

        assertEquals(1, fakeService.nowPlayingCalls.size)
        val call = fakeService.nowPlayingCalls[0]
        assertEquals("Artist", call.track.artistName)
        assertEquals("Track", call.track.trackName)
        assertEquals("Album", call.track.releaseName)
    }

    @Test
    fun playbackStartedWithNullMetadataDoesNotSubmit() = testScope.runTest {
        scrobbleManager.onPlaybackStarted(null, null, null, null, 200_000)
        advanceUntilIdle()

        assertEquals(0, fakeService.nowPlayingCalls.size)
    }

    @Test
    fun resumingSameTrackResubmitsNowPlaying() = testScope.runTest {
        scrobbleManager.onPlaybackStarted("Artist", "Track", "Album", 1, 120_000)
        advanceUntilIdle()
        assertEquals(1, fakeService.nowPlayingCalls.size)

        scrobbleManager.onPlaybackStarted("Artist", "Track", "Album", 1, 120_000)
        advanceUntilIdle()
        assertEquals(2, fakeService.nowPlayingCalls.size)
    }

    @Test
    fun resumingSameTrackDoesNotResetScrobbleState() = testScope.runTest {
        scrobbleManager.onPlaybackStarted("Artist", "Track", "Album", 1, 120_000)
        advanceUntilIdle()

        // Scrobble the track
        scrobbleManager.onPlaybackPositionUpdated(60_000)
        advanceUntilIdle()
        assertEquals(1, fakeService.listenCalls.size)

        // Resume same track — should not scrobble again
        scrobbleManager.onPlaybackStarted("Artist", "Track", "Album", 1, 120_000)
        scrobbleManager.onPlaybackPositionUpdated(90_000)
        advanceUntilIdle()
        assertEquals(1, fakeService.listenCalls.size)
    }

    // --- Scrobble threshold ---

    @Test
    fun scrobblesAtFiftyPercentForShortTrack() = testScope.runTest {
        scrobbleManager.onPlaybackStarted("Artist", "Track", "Album", 1, 120_000)
        advanceUntilIdle()

        scrobbleManager.onPlaybackPositionUpdated(59_000)
        advanceUntilIdle()
        assertEquals(0, fakeService.listenCalls.size)

        scrobbleManager.onPlaybackPositionUpdated(60_000)
        advanceUntilIdle()
        assertEquals(1, fakeService.listenCalls.size)
    }

    @Test
    fun scrobblesAtFourMinutesForLongTrack() = testScope.runTest {
        scrobbleManager.onPlaybackStarted("Artist", "Track", "Album", 1, 20 * 60 * 1000L)
        advanceUntilIdle()

        scrobbleManager.onPlaybackPositionUpdated(239_000)
        advanceUntilIdle()
        assertEquals(0, fakeService.listenCalls.size)

        scrobbleManager.onPlaybackPositionUpdated(240_000)
        advanceUntilIdle()
        assertEquals(1, fakeService.listenCalls.size)
    }

    @Test
    fun usesLowerOfFiftyPercentOrFourMinutes() = testScope.runTest {
        scrobbleManager.onPlaybackStarted("Artist", "Track", "Album", 1, 6 * 60 * 1000L)
        advanceUntilIdle()

        scrobbleManager.onPlaybackPositionUpdated(179_000)
        advanceUntilIdle()
        assertEquals(0, fakeService.listenCalls.size)

        scrobbleManager.onPlaybackPositionUpdated(180_000)
        advanceUntilIdle()
        assertEquals(1, fakeService.listenCalls.size)
    }

    @Test
    fun doesNotScrobbleSameTrackTwice() = testScope.runTest {
        scrobbleManager.onPlaybackStarted("Artist", "Track", "Album", 1, 120_000)
        advanceUntilIdle()

        scrobbleManager.onPlaybackPositionUpdated(60_000)
        advanceUntilIdle()
        assertEquals(1, fakeService.listenCalls.size)

        scrobbleManager.onPlaybackPositionUpdated(90_000)
        advanceUntilIdle()
        assertEquals(1, fakeService.listenCalls.size)
    }

    @Test
    fun doesNotScrobbleZeroDurationTrack() = testScope.runTest {
        scrobbleManager.onPlaybackStarted("Artist", "Track", "Album", 1, 0)
        advanceUntilIdle()

        scrobbleManager.onPlaybackPositionUpdated(1000)
        advanceUntilIdle()
        assertEquals(0, fakeService.listenCalls.size)
    }

    @Test
    fun scrobblesVeryShortTrackAtFiftyPercent() = testScope.runTest {
        scrobbleManager.onPlaybackStarted("Artist", "Track", "Album", 1, 20_000)
        advanceUntilIdle()

        scrobbleManager.onPlaybackPositionUpdated(10_000)
        advanceUntilIdle()
        assertEquals(1, fakeService.listenCalls.size)
    }

    // --- Track change resets state ---

    @Test
    fun newTrackResetsScrobbleState() = testScope.runTest {
        scrobbleManager.onPlaybackStarted("Artist1", "Track1", "Album1", 1, 120_000)
        advanceUntilIdle()

        scrobbleManager.onPlaybackPositionUpdated(60_000)
        advanceUntilIdle()
        assertEquals(1, fakeService.listenCalls.size)

        // New track starts playing
        scrobbleManager.onPlaybackStarted("Artist2", "Track2", "Album2", 2, 120_000)
        advanceUntilIdle()
        assertEquals(2, fakeService.nowPlayingCalls.size)
        assertEquals("Artist2", fakeService.nowPlayingCalls[1].track.artistName)

        // Can scrobble the new track
        scrobbleManager.onPlaybackPositionUpdated(60_000)
        advanceUntilIdle()
        assertEquals(2, fakeService.listenCalls.size)
        assertEquals("Artist2", fakeService.listenCalls[1].track.artistName)
    }

    // --- Track transition during continuous playback ---

    @Test
    fun trackTransitionDuringPlaybackSubmitsNewNowPlaying() = testScope.runTest {
        scrobbleManager.onPlaybackStarted("Artist1", "Track1", "Album1", 1, 200_000)
        advanceUntilIdle()
        assertEquals(1, fakeService.nowPlayingCalls.size)

        // Simulate position updates during first track
        scrobbleManager.onPlaybackPositionUpdated(50_000)
        advanceUntilIdle()

        // Track changes without playback stopping (continuous playback)
        scrobbleManager.onPlaybackStarted("Artist2", "Track2", "Album2", 2, 180_000)
        advanceUntilIdle()
        assertEquals(2, fakeService.nowPlayingCalls.size)
        assertEquals("Artist2", fakeService.nowPlayingCalls[1].track.artistName)
        assertEquals("Track2", fakeService.nowPlayingCalls[1].track.trackName)
    }

    @Test
    fun trackTransitionResetsScrobbleProgressSoNewTrackCanScrobble() = testScope.runTest {
        scrobbleManager.onPlaybackStarted("Artist1", "Track1", "Album1", 1, 120_000)
        advanceUntilIdle()

        // First track not yet at scrobble threshold
        scrobbleManager.onPlaybackPositionUpdated(50_000)
        advanceUntilIdle()
        assertEquals(0, fakeService.listenCalls.size)

        // Track transitions — position resets
        scrobbleManager.onPlaybackStarted("Artist2", "Track2", "Album2", 2, 120_000)
        advanceUntilIdle()

        // New track reaches its own scrobble threshold
        scrobbleManager.onPlaybackPositionUpdated(60_000)
        advanceUntilIdle()
        assertEquals(1, fakeService.listenCalls.size)
        assertEquals("Artist2", fakeService.listenCalls[0].track.artistName)
    }

    @Test
    fun trackTransitionCancelsPendingClearNowPlaying() = testScope.runTest {
        scrobbleManager.onPlaybackStarted("Artist1", "Track1", "Album1", 1, 120_000)
        advanceUntilIdle()

        // Brief stop (e.g. buffering between tracks)
        scrobbleManager.onPlaybackStopped()
        advanceTimeBy(10_000)
        assertEquals(0, fakeService.clearNowPlayingCount)

        // New track starts before clear fires
        scrobbleManager.onPlaybackStarted("Artist2", "Track2", "Album2", 2, 120_000)
        advanceTimeBy(NOW_PLAYING_CLEAR_DELAY_MS)
        assertEquals(0, fakeService.clearNowPlayingCount)
    }

    // --- Playback stopped ---

    @Test
    fun playbackStoppedClearsNowPlayingAfterDelay() = testScope.runTest {
        scrobbleManager.onPlaybackStarted("Artist", "Track", "Album", 1, 120_000)
        advanceUntilIdle()

        scrobbleManager.onPlaybackStopped()
        advanceTimeBy(NOW_PLAYING_CLEAR_DELAY_MS - 1)
        assertEquals(0, fakeService.clearNowPlayingCount)

        advanceTimeBy(1)
        runCurrent()
        assertEquals(1, fakeService.clearNowPlayingCount)
    }

    @Test
    fun resumingBeforeDelayCancelsClear() = testScope.runTest {
        scrobbleManager.onPlaybackStarted("Artist", "Track", "Album", 1, 120_000)
        advanceUntilIdle()

        scrobbleManager.onPlaybackStopped()
        advanceTimeBy(15_000)
        assertEquals(0, fakeService.clearNowPlayingCount)

        // Resume before 30s — clear should be cancelled
        scrobbleManager.onPlaybackStarted("Artist", "Track", "Album", 1, 120_000)
        advanceUntilIdle()
        assertEquals(0, fakeService.clearNowPlayingCount)

        // Wait past the original 30s — should still not clear
        advanceTimeBy(NOW_PLAYING_CLEAR_DELAY_MS)
        assertEquals(0, fakeService.clearNowPlayingCount)
    }

    @Test
    fun multipleStopsOnlyFiresOneClear() = testScope.runTest {
        scrobbleManager.onPlaybackStarted("Artist", "Track", "Album", 1, 120_000)
        advanceUntilIdle()

        scrobbleManager.onPlaybackStopped()
        advanceTimeBy(10_000)
        scrobbleManager.onPlaybackStopped()
        advanceUntilIdle()

        assertEquals(1, fakeService.clearNowPlayingCount)
    }

    // --- Multiple services ---

    @Test
    fun scrobblesAllServices() = testScope.runTest {
        val secondService = FakeScrobbleService()
        scrobbleManager = ScrobbleManager(listOf(fakeService, secondService))
        scrobbleManager.scope = testScope

        scrobbleManager.onPlaybackStarted("Artist", "Track", "Album", 1, 120_000)
        advanceUntilIdle()

        assertEquals(1, fakeService.nowPlayingCalls.size)
        assertEquals(1, secondService.nowPlayingCalls.size)

        scrobbleManager.onPlaybackPositionUpdated(60_000)
        advanceUntilIdle()

        assertEquals(1, fakeService.listenCalls.size)
        assertEquals(1, secondService.listenCalls.size)
    }
}
