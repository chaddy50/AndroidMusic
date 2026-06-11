package com.chaddy50.musicapp.data.api.listenBrainz

import com.chaddy50.musicapp.data.scrobbling.ScrobbleTrackMetadata
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class ListenBrainzRepositoryTest {

    private lateinit var service: FakeListenBrainzService
    private lateinit var preferences: FakeListenBrainzPreferences
    private lateinit var repository: ListenBrainzRepository
    private lateinit var testScope: TestScope

    @Before
    fun setup() {
        service = FakeListenBrainzService()
        preferences = FakeListenBrainzPreferences(
            initialToken = "test-token",
        )
        testScope = TestScope()
        repository = ListenBrainzRepository(service, preferences, testScope)
    }

    // --- validateToken ---

    @Test
    fun validateTokenReturnsValidResponse() = testScope.runTest {
        advanceUntilIdle() // let init complete

        val response = repository.validateToken("good-token")

        assertNotNull(response)
        assertTrue(response!!.valid)
        assertEquals("testuser", response.user_name)
    }

    @Test
    fun validateTokenReturnsInvalidResponse() = testScope.runTest {
        service = FakeListenBrainzService(
            onValidateToken = { ValidateTokenResponse(200, "Token invalid.", false, null) }
        )
        repository = ListenBrainzRepository(service, preferences, testScope)
        advanceUntilIdle()

        val response = repository.validateToken("bad-token")

        assertNotNull(response)
        assertFalse(response!!.valid)
        assertNull(response.user_name)
    }

    @Test
    fun validateTokenReturnsNullOnNetworkError() = testScope.runTest {
        service = FakeListenBrainzService(
            onValidateToken = { throw IOException("Network error") }
        )
        repository = ListenBrainzRepository(service, preferences, testScope)
        advanceUntilIdle()

        val response = repository.validateToken("any-token")

        assertNull(response)
    }

    // --- init validates saved token ---

    @Test
    fun initValidatesSavedToken() = testScope.runTest {
        advanceUntilIdle()

        val state = repository.tokenValidationState.value
        assertTrue(state is TokenValidationState.Valid)
        assertEquals("testuser", (state as TokenValidationState.Valid).userName)
    }

    @Test
    fun initStaysIdleWhenNoToken() = testScope.runTest {
        preferences = FakeListenBrainzPreferences(initialToken = null)
        repository = ListenBrainzRepository(service, preferences, testScope)
        advanceUntilIdle()

        assertEquals(TokenValidationState.Idle, repository.tokenValidationState.value)
    }

    // --- submitListen ---

    @Test
    fun submitListenSendsCorrectRequest() = testScope.runTest {
        advanceUntilIdle()

        val result = repository.submitListen(ScrobbleTrackMetadata("Artist", "Track", "Album", 1, 120_000), 1700000000)

        assertTrue(result)
        // Filter to only "single" type calls (init sends a validate call)
        val submitCalls = service.submitCalls
        val listenCall = submitCalls.last()
        assertEquals("Token test-token", listenCall.first)
        assertEquals("single", listenCall.second.listen_type)
        assertEquals(1700000000L, listenCall.second.payload[0].listened_at)
        assertEquals("Artist", listenCall.second.payload[0].track_metadata.artist_name)
        assertEquals("Track", listenCall.second.payload[0].track_metadata.track_name)
        assertEquals("Album", listenCall.second.payload[0].track_metadata.release_name)
    }

    @Test
    fun submitListenReturnsFalseWhenNoToken() = testScope.runTest {
        advanceUntilIdle()
        preferences.setToken(null)

        val result = repository.submitListen(ScrobbleTrackMetadata("Artist", "Track", "Album", 1, 120_000), 1700000000)

        assertFalse(result)
    }

    @Test
    fun submitListenReturnsFalseOnNetworkError() = testScope.runTest {
        advanceUntilIdle()
        service = FakeListenBrainzService(
            onSubmitListens = { _, _ -> throw IOException("Network error") }
        )
        repository = ListenBrainzRepository(service, preferences, testScope)
        advanceUntilIdle()

        val result = repository.submitListen(ScrobbleTrackMetadata("Artist", "Track", "Album", 1, 120_000), 1700000000)

        assertFalse(result)
    }

    // --- submitNowPlaying ---

    @Test
    fun submitNowPlayingSendsCorrectRequest() = testScope.runTest {
        advanceUntilIdle()

        val result = repository.submitNowPlaying(ScrobbleTrackMetadata("Artist", "Track", "Album", 1, 120_000))

        assertTrue(result)
        val listenCall = service.submitCalls.last()
        assertEquals("Token test-token", listenCall.first)
        assertEquals("playing_now", listenCall.second.listen_type)
        assertNull(listenCall.second.payload[0].listened_at)
        assertEquals("Artist", listenCall.second.payload[0].track_metadata.artist_name)
    }

    @Test
    fun submitNowPlayingReturnsFalseWhenNoToken() = testScope.runTest {
        advanceUntilIdle()
        preferences.setToken(null)

        val result = repository.submitNowPlaying(ScrobbleTrackMetadata("Artist", "Track", "Album", 1, 120_000))

        assertFalse(result)
    }

    // --- saveToken ---

    @Test
    fun saveTokenValidatesAndStoresOnSuccess() = testScope.runTest {
        advanceUntilIdle()

        repository.saveToken("new-token")
        advanceUntilIdle()

        val state = repository.tokenValidationState.value
        assertTrue(state is TokenValidationState.Valid)
        assertEquals("new-token", preferences.getToken())
    }

    @Test
    fun saveTokenSetsInvalidOnBadToken() = testScope.runTest {
        service = FakeListenBrainzService(
            onValidateToken = { ValidateTokenResponse(200, "Token invalid.", false, null) }
        )
        repository = ListenBrainzRepository(service, preferences, testScope)
        advanceUntilIdle()

        repository.saveToken("bad-token")
        advanceUntilIdle()

        assertEquals(TokenValidationState.Invalid, repository.tokenValidationState.value)
    }

    // --- logout ---

    @Test
    fun logoutClearsTokenAndResetsState() = testScope.runTest {
        advanceUntilIdle()
        assertTrue(repository.tokenValidationState.value is TokenValidationState.Valid)

        repository.logout()
        advanceUntilIdle()

        assertEquals(TokenValidationState.Idle, repository.tokenValidationState.value)
        assertNull(preferences.getToken())
    }
}
