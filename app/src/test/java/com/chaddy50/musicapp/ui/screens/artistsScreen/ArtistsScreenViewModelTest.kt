package com.chaddy50.musicapp.ui.screens.artistsScreen

import androidx.lifecycle.SavedStateHandle
import com.chaddy50.musicapp.data.ClassicalGenreConfig
import com.chaddy50.musicapp.data.entity.AlbumArtist
import com.chaddy50.musicapp.data.entity.Composer
import com.chaddy50.musicapp.data.entity.Genre
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.ComposerRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.repository.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import com.chaddy50.musicapp.fakes.FakeAlbumArtistDao
import com.chaddy50.musicapp.fakes.FakeArtworkDownloader
import com.chaddy50.musicapp.fakes.FakeAudioDbRepository
import com.chaddy50.musicapp.fakes.FakeComposerDao
import com.chaddy50.musicapp.fakes.FakeGenreDao
import com.chaddy50.musicapp.fakes.FakeOpenOpusRepository
import com.chaddy50.musicapp.fakes.FakePlaylistDao
import com.chaddy50.musicapp.fakes.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class ArtistsScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val albumArtistsFlow = MutableStateFlow<List<AlbumArtist>>(emptyList())
    private val genresFlow = MutableStateFlow<List<Genre>>(emptyList())
    private val composersFlow = MutableStateFlow<List<Composer>>(emptyList())

    private fun createViewModel(
        genreId: Long = 1L,
        classicalGenreId: Long? = null,
        playlistDao: FakePlaylistDao = FakePlaylistDao(),
        audioDbRepository: FakeAudioDbRepository = FakeAudioDbRepository(),
        openOpusRepository: FakeOpenOpusRepository = FakeOpenOpusRepository(),
        artworkDownloader: FakeArtworkDownloader = FakeArtworkDownloader(),
    ): ArtistsScreenViewModel {
        val genreDao = FakeGenreDao(allGenres = genresFlow)
        val albumArtistDao = FakeAlbumArtistDao(albumArtistsFlow)
        val composerDao = FakeComposerDao(composersFlow)

        val config = ClassicalGenreConfig().apply { this.classicalGenreId = classicalGenreId }
        val savedStateHandle = SavedStateHandle(
            mapOf("genreId" to genreId, "title" to "Test")
        )
        return ArtistsScreenViewModel(
            savedStateHandle,
            config,
            AlbumArtistRepository(albumArtistDao, genreDao, audioDbRepository, Dispatchers.Unconfined),
            GenreRepository(genreDao),
            PlaylistRepository(playlistDao),
            ComposerRepository(composerDao, openOpusRepository, artworkDownloader, albumArtistDao),
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

    @Test
    fun entityHeaderIncludesPlaylistMembershipData() = runTest {
        val playlistIdsFlow = MutableStateFlow(listOf(1L, 3L))
        val playlistDao = FakePlaylistDao(playlistIdsContainingGenreFlow = playlistIdsFlow)
        val vm = createViewModel(genreId = 5L, playlistDao = playlistDao)
        backgroundScope.launch { vm.entityHeaderState.collect() }

        genresFlow.value = listOf(Genre(id = 5L, name = "Rock"))
        albumArtistsFlow.value = listOf(
            AlbumArtist(id = 1, name = "Led Zeppelin", sortName = "Led Zeppelin", genreId = 5),
        )
        advanceUntilIdle()

        assertEquals(setOf(1L, 3L), vm.entityHeaderState.value.playlistsThatEntityIsAlreadyIn)
    }

    // --- Eager portrait fetching ---

    @Test
    fun fetchesPortraitForNonClassicalArtistsWithoutPortrait() = runTest {
        val audioDbRepo = FakeAudioDbRepository(portraitUrl = "/downloaded/portrait.jpg")
        genresFlow.value = listOf(Genre(id = 5L, name = "Rock"))
        albumArtistsFlow.value = listOf(
            AlbumArtist(id = 1, name = "Led Zeppelin", sortName = "Led Zeppelin", genreId = 5),
        )

        val vm = createViewModel(genreId = 5L, audioDbRepository = audioDbRepo)
        backgroundScope.launch { vm.uiState.collect() }

        // Wait for the IO coroutine that fetches portraits to complete
        val artist = withContext(Dispatchers.Default) {
            vm.uiState.first { it.artists.any { a -> a.portraitPath != null } }
        }.artists.find { it.id == 1L }
        assertEquals("/downloaded/portrait.jpg", artist?.portraitPath)
    }

    @Test
    fun skipsPortraitFetchForArtistsThatAlreadyHavePortrait() = runTest {
        val audioDbRepo = FakeAudioDbRepository(portraitUrl = "/new/portrait.jpg")
        genresFlow.value = listOf(Genre(id = 5L, name = "Rock"))
        albumArtistsFlow.value = listOf(
            AlbumArtist(id = 1, name = "Led Zeppelin", sortName = "Led Zeppelin", genreId = 5, portraitPath = "/existing/portrait.jpg"),
        )

        val vm = createViewModel(genreId = 5L, audioDbRepository = audioDbRepo)
        backgroundScope.launch { vm.uiState.collect() }
        advanceUntilIdle()

        val artist = vm.uiState.value.artists.find { it.id == 1L }
        assertEquals("/existing/portrait.jpg", artist?.portraitPath)
    }

    @Test
    fun fetchesComposerForClassicalArtistsWithoutComposerRecord() = runTest {
        val openOpusRepo = FakeOpenOpusRepository(
            composer = com.chaddy50.musicapp.data.api.openOpus.OpenOpusComposer(
                id = 196, name = "Bach", completeName = "Johann Sebastian Bach",
                birthDate = "1685-03-31", deathDate = "1750-07-28", epoch = "Baroque",
                portraitUrl = "https://example.com/bach.jpg",
            )
        )
        val artworkDownloader = FakeArtworkDownloader(resultPath = "/portraits/bach.jpg")
        genresFlow.value = listOf(Genre(id = 10L, name = "Classical"))
        albumArtistsFlow.value = listOf(
            AlbumArtist(id = 1, name = "Bach", sortName = "Bach", genreId = 10),
        )

        val vm = createViewModel(
            genreId = 10L, classicalGenreId = 10L,
            openOpusRepository = openOpusRepo, artworkDownloader = artworkDownloader,
        )
        backgroundScope.launch { vm.uiState.collect() }

        // Wait for the IO coroutine that fetches composer + syncs portrait
        val artist = withContext(Dispatchers.Default) {
            vm.uiState.first { it.artists.any { a -> a.portraitPath != null } }
        }.artists.find { it.id == 1L }
        assertEquals("/portraits/bach.jpg", artist?.portraitPath)
    }

    @Test
    fun skipsComposerFetchForClassicalArtistsThatAlreadyHaveComposer() = runTest {
        val openOpusRepo = FakeOpenOpusRepository(
            composer = com.chaddy50.musicapp.data.api.openOpus.OpenOpusComposer(
                id = 196, name = "Bach", completeName = "Johann Sebastian Bach",
                birthDate = "1685-03-31", deathDate = "1750-07-28", epoch = "Baroque",
                portraitUrl = "https://example.com/bach.jpg",
            )
        )
        genresFlow.value = listOf(Genre(id = 10L, name = "Classical"))
        albumArtistsFlow.value = listOf(
            AlbumArtist(id = 1, name = "Bach", sortName = "Bach", genreId = 10, portraitPath = "/existing/bach.jpg"),
        )
        composersFlow.value = listOf(
            Composer(id = 1, albumArtistId = 1, openOpusId = 196, completeName = "Bach",
                birthYear = "1685", deathYear = "1750", epoch = "Baroque", portraitPath = "/existing/bach.jpg"),
        )

        val vm = createViewModel(genreId = 10L, classicalGenreId = 10L, openOpusRepository = openOpusRepo)
        backgroundScope.launch { vm.uiState.collect() }
        advanceUntilIdle()

        // Portrait should remain unchanged
        val artist = vm.uiState.value.artists.find { it.id == 1L }
        assertEquals("/existing/bach.jpg", artist?.portraitPath)
    }

    @Test
    fun doesNotFetchPortraitsForGenresInDenyList() = runTest {
        val audioDbRepo = FakeAudioDbRepository(portraitUrl = "/downloaded/portrait.jpg")
        genresFlow.value = listOf(Genre(id = 5L, name = "Anime"))
        albumArtistsFlow.value = listOf(
            AlbumArtist(id = 1, name = "Some Artist", sortName = "Some Artist", genreId = 5),
        )

        val vm = createViewModel(genreId = 5L, audioDbRepository = audioDbRepo)
        backgroundScope.launch { vm.uiState.collect() }
        advanceUntilIdle()

        val artist = vm.uiState.value.artists.find { it.id == 1L }
        assertNull(artist?.portraitPath)
    }
}
