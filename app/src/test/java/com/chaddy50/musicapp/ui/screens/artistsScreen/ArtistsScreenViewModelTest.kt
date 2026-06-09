package com.chaddy50.musicapp.ui.screens.artistsScreen

import androidx.lifecycle.SavedStateHandle
import com.chaddy50.musicapp.data.ClassicalGenreConfig
import com.chaddy50.musicapp.data.api.audioDb.AudioDbRepository
import com.chaddy50.musicapp.data.entity.AlbumArtist
import com.chaddy50.musicapp.data.entity.Genre
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.repository.PlaylistRepository
import com.chaddy50.musicapp.data.util.ArtworkDownloader
import kotlinx.coroutines.Dispatchers
import com.chaddy50.musicapp.fakes.FakeAlbumArtistDao
import com.chaddy50.musicapp.fakes.FakeGenreDao
import com.chaddy50.musicapp.fakes.FakePlaylistDao
import com.chaddy50.musicapp.fakes.StubAudioDbService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import com.chaddy50.musicapp.fakes.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class ArtistsScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val albumArtistsFlow = MutableStateFlow<List<AlbumArtist>>(emptyList())
    private val genresFlow = MutableStateFlow<List<Genre>>(emptyList())

    private fun createViewModel(
        genreId: Long = 1L,
        classicalGenreId: Long? = null,
    ): ArtistsScreenViewModel {
        val genreDao = FakeGenreDao(allGenres = genresFlow)
        val albumArtistDao = FakeAlbumArtistDao(albumArtistsFlow)
        val artworkDownloader = ArtworkDownloader(RuntimeEnvironment.getApplication())
        val audioDbRepository = AudioDbRepository(StubAudioDbService(), artworkDownloader)

        val config = ClassicalGenreConfig().apply { this.classicalGenreId = classicalGenreId }
        val savedStateHandle = SavedStateHandle(
            mapOf("genreId" to genreId, "title" to "Test")
        )
        return ArtistsScreenViewModel(
            savedStateHandle,
            config,
            AlbumArtistRepository(albumArtistDao, genreDao, audioDbRepository, Dispatchers.Unconfined),
            GenreRepository(genreDao),
            PlaylistRepository(FakePlaylistDao()),
        )
    }

    @Test
    fun initialStateIsLoading() {
        val vm = createViewModel()
        assertTrue(vm.uiState.value.isLoading)
    }

    @Test
    fun emitsArtistsFromRepository() = runTest {
        val vm = createViewModel(genreId = 5L)
        backgroundScope.launch { vm.uiState.collect() }

        genresFlow.value = listOf(Genre(id = 5L, name = "Rock"))
        albumArtistsFlow.value = listOf(
            AlbumArtist(id = 1, name = "Led Zeppelin", sortName = "Led Zeppelin", genreId = 5),
            AlbumArtist(id = 2, name = "Pink Floyd", sortName = "Pink Floyd", genreId = 5),
        )
        advanceUntilIdle()

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.artists.size)
        assertEquals("Rock", state.screenTitle)
    }

    @Test
    fun screenTitleFallsBackToArtistsWhenGenreNameNull() = runTest {
        val vm = createViewModel(genreId = 99L)
        backgroundScope.launch { vm.uiState.collect() }
        advanceUntilIdle()

        assertEquals("Artists", vm.uiState.value.screenTitle)
    }

    @Test
    fun entityHeaderShowsGenreNameAndArtistCount() = runTest {
        val vm = createViewModel(genreId = 5L)
        backgroundScope.launch { vm.entityHeaderState.collect() }

        genresFlow.value = listOf(Genre(id = 5L, name = "Rock"))
        albumArtistsFlow.value = listOf(
            AlbumArtist(id = 1, name = "Led Zeppelin", sortName = "Led Zeppelin", genreId = 5),
            AlbumArtist(id = 2, name = "Pink Floyd", sortName = "Pink Floyd", genreId = 5),
        )
        advanceUntilIdle()

        val header = vm.entityHeaderState.value
        assertEquals("Rock", header.title)
        assertEquals("2 artists", header.subtitle)
    }

    @Test
    fun entityHeaderShowsComposersLabelForClassicalGenre() = runTest {
        val vm = createViewModel(genreId = 10L, classicalGenreId = 10L)
        backgroundScope.launch { vm.entityHeaderState.collect() }

        genresFlow.value = listOf(Genre(id = 10L, name = "Classical"))
        albumArtistsFlow.value = listOf(
            AlbumArtist(id = 1, name = "Bach", sortName = "Bach", genreId = 10),
        )
        advanceUntilIdle()

        val header = vm.entityHeaderState.value
        assertEquals("Classical", header.title)
        assertEquals("1 composers", header.subtitle)
    }

    @Test
    fun entityHeaderFallsBackWhenGenreNotFound() = runTest {
        val vm = createViewModel(genreId = 99L)
        backgroundScope.launch { vm.entityHeaderState.collect() }
        advanceUntilIdle()

        assertEquals("Genre", vm.entityHeaderState.value.title)
    }
}
