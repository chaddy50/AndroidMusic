package com.chaddy50.musicapp.ui.screens.albumsScreen

import androidx.lifecycle.SavedStateHandle
import com.chaddy50.musicapp.data.ClassicalGenreConfig
import com.chaddy50.musicapp.data.entity.Album
import com.chaddy50.musicapp.data.entity.AlbumArtist
import com.chaddy50.musicapp.data.entity.Composer
import com.chaddy50.musicapp.data.entity.Genre
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.AlbumRepository
import com.chaddy50.musicapp.data.repository.ComposerRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.repository.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import com.chaddy50.musicapp.fakes.FakeAlbumArtistDao
import com.chaddy50.musicapp.fakes.FakeAlbumDao
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

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class AlbumsScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val albumsFlow = MutableStateFlow<List<Album>>(emptyList())
    private val albumArtistsFlow = MutableStateFlow<List<AlbumArtist>>(emptyList())
    private val genresFlow = MutableStateFlow<List<Genre>>(emptyList())
    private val composersFlow = MutableStateFlow<List<Composer>>(emptyList())

    private fun createViewModel(
        genreId: Long = 1L,
        albumArtistId: Long = 1L,
        classicalGenreId: Long? = null,
    ): AlbumsScreenViewModel {
        val genreDao = FakeGenreDao(allGenres = genresFlow)
        val albumArtistDao = FakeAlbumArtistDao(albumArtistsFlow)
        val albumDao = FakeAlbumDao(albumsFlow)
        val composerDao = FakeComposerDao(composersFlow)
        val artworkDownloader = FakeArtworkDownloader()
        val audioDbRepository = FakeAudioDbRepository()
        val openOpusRepository = FakeOpenOpusRepository()

        val config = ClassicalGenreConfig().apply { this.classicalGenreId = classicalGenreId }
        val savedStateHandle = SavedStateHandle(
            mapOf("genreId" to genreId, "albumArtistId" to albumArtistId, "title" to "Test")
        )
        return AlbumsScreenViewModel(
            savedStateHandle,
            config,
            AlbumRepository(albumDao),
            AlbumArtistRepository(albumArtistDao, genreDao, audioDbRepository, Dispatchers.Unconfined),
            GenreRepository(genreDao),
            PlaylistRepository(FakePlaylistDao()),
            ComposerRepository(composerDao, openOpusRepository, artworkDownloader),
        )
    }

    // --- uiState ---

    @Test
    fun initialStateIsLoading() {
        val vm = createViewModel()
        assertTrue(vm.uiState.value.isLoading)
    }

    @Test
    fun emitsAlbumsForArtist() = runTest {
        val vm = createViewModel(albumArtistId = 1L)
        backgroundScope.launch { vm.uiState.collect() }

        albumArtistsFlow.value = listOf(
            AlbumArtist(id = 1, name = "Pink Floyd", sortName = "Pink Floyd", genreId = 1, portraitPath = "exists"),
        )
        albumsFlow.value = listOf(
            Album(id = 1, title = "The Wall", catalogueNumber = null, artistId = 1, year = "1979"),
            Album(id = 2, title = "Animals", catalogueNumber = null, artistId = 1, year = "1977"),
        )
        advanceUntilIdle()

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.albums.size)
        assertEquals("Pink Floyd", state.screenTitle)
    }

    @Test
    fun screenTitleFallsBackToAlbumsWhenArtistNameNull() = runTest {
        val vm = createViewModel(albumArtistId = 99L)
        backgroundScope.launch { vm.uiState.collect() }
        advanceUntilIdle()

        assertEquals("Albums", vm.uiState.value.screenTitle)
    }

    @Test
    fun screenTitleIncludesSubGenreWhenSelected() = runTest {
        val vm = createViewModel(genreId = 10L, albumArtistId = 1L, classicalGenreId = 10L)
        backgroundScope.launch { vm.uiState.collect() }

        albumArtistsFlow.value = listOf(
            AlbumArtist(id = 1, name = "Bach", sortName = "Bach", genreId = 10),
        )
        genresFlow.value = listOf(
            Genre(id = 10L, name = "Classical"),
            Genre(id = 20L, name = "Orchestral", parentGenreId = 10L),
        )
        // Provide a composer so the one-shot fetch doesn't trigger
        composersFlow.value = listOf(
            Composer(id = 1, albumArtistId = 1, openOpusId = 1, completeName = "Bach", birthYear = null, deathYear = null, epoch = null, portraitPath = null)
        )

        vm.updateSelectedSubGenreId(20L)
        advanceUntilIdle()

        assertEquals("Bach - Orchestral", vm.uiState.value.screenTitle)
    }

    @Test
    fun clearingSubGenreResetsTitleToArtistName() = runTest {
        val vm = createViewModel(genreId = 10L, albumArtistId = 1L, classicalGenreId = 10L)
        backgroundScope.launch { vm.uiState.collect() }

        albumArtistsFlow.value = listOf(
            AlbumArtist(id = 1, name = "Bach", sortName = "Bach", genreId = 10),
        )
        genresFlow.value = listOf(
            Genre(id = 10L, name = "Classical"),
            Genre(id = 20L, name = "Orchestral", parentGenreId = 10L),
        )
        composersFlow.value = listOf(
            Composer(id = 1, albumArtistId = 1, openOpusId = 1, completeName = "Bach", birthYear = null, deathYear = null, epoch = null, portraitPath = null)
        )

        vm.updateSelectedSubGenreId(20L)
        advanceUntilIdle()
        assertEquals("Bach - Orchestral", vm.uiState.value.screenTitle)

        vm.updateSelectedSubGenreId(null)
        advanceUntilIdle()
        assertEquals("Bach", vm.uiState.value.screenTitle)
    }

    // --- entityHeaderState ---

    @Test
    fun entityHeaderShowsNonClassicalArtistInfo() = runTest {
        val vm = createViewModel(genreId = 1L, albumArtistId = 1L)
        backgroundScope.launch { vm.entityHeaderState.collect() }

        genresFlow.value = listOf(Genre(id = 1L, name = "Rock"))
        albumArtistsFlow.value = listOf(
            AlbumArtist(id = 1, name = "Pink Floyd", sortName = "Pink Floyd", genreId = 1, portraitPath = "/portraits/pf.jpg"),
        )
        albumsFlow.value = listOf(
            Album(id = 1, title = "The Wall", catalogueNumber = null, artistId = 1, year = "1979"),
            Album(id = 2, title = "Animals", catalogueNumber = null, artistId = 1, year = "1977"),
        )
        advanceUntilIdle()

        val header = vm.entityHeaderState.value
        assertEquals("Pink Floyd", header.title)
        assertEquals("Rock", header.subtitle)
        assertEquals("2 albums", header.details)
    }

    @Test
    fun entityHeaderShowsComposerInfoForClassical() = runTest {
        val vm = createViewModel(genreId = 10L, albumArtistId = 1L, classicalGenreId = 10L)
        backgroundScope.launch { vm.entityHeaderState.collect() }

        genresFlow.value = listOf(Genre(id = 10L, name = "Classical"))
        albumArtistsFlow.value = listOf(
            AlbumArtist(id = 1, name = "Bach", sortName = "Bach", genreId = 10),
        )
        composersFlow.value = listOf(
            Composer(
                id = 1, albumArtistId = 1, openOpusId = 196,
                completeName = "Johann Sebastian Bach",
                birthYear = "1685", deathYear = "1750", epoch = "Baroque",
                portraitPath = "/portraits/bach.jpg",
            )
        )
        albumsFlow.value = listOf(
            Album(id = 1, title = "Goldberg Variations", catalogueNumber = 988, artistId = 1, year = "1741"),
        )
        advanceUntilIdle()

        val header = vm.entityHeaderState.value
        assertEquals("Johann Sebastian Bach", header.title)
        assertEquals("Baroque - 1685–1750", header.subtitle)
        assertEquals("1 works", header.details)
        assertEquals("/portraits/bach.jpg", header.artworkPath)
    }

    @Test
    fun entityHeaderFallsBackWhenNoComposerForClassical() = runTest {
        val vm = createViewModel(genreId = 10L, albumArtistId = 1L, classicalGenreId = 10L)
        backgroundScope.launch { vm.entityHeaderState.collect() }

        genresFlow.value = listOf(Genre(id = 10L, name = "Classical"))
        albumArtistsFlow.value = listOf(
            AlbumArtist(id = 1, name = "Bach", sortName = "Bach", genreId = 10),
        )
        albumsFlow.value = listOf(
            Album(id = 1, title = "Goldberg Variations", catalogueNumber = 988, artistId = 1, year = "1741"),
        )
        advanceUntilIdle()

        val header = vm.entityHeaderState.value
        assertEquals("Bach", header.title)
        assertEquals("Classical", header.subtitle)
        assertEquals("1 works", header.details)
    }

    @Test
    fun entityHeaderFallsBackWhenArtistNotFound() = runTest {
        val vm = createViewModel(genreId = 1L, albumArtistId = 99L)
        backgroundScope.launch { vm.entityHeaderState.collect() }
        advanceUntilIdle()

        assertEquals("Artist", vm.entityHeaderState.value.title)
    }

    // --- isClassical ---

    @Test
    fun isClassicalTrueWhenGenreMatchesClassicalConfig() {
        val vm = createViewModel(genreId = 10L, classicalGenreId = 10L)
        assertTrue(vm.isClassical)
    }

    @Test
    fun isClassicalFalseWhenGenreDoesNotMatch() {
        val vm = createViewModel(genreId = 1L, classicalGenreId = 10L)
        assertFalse(vm.isClassical)
    }

    @Test
    fun isClassicalFalseWhenConfigIsNull() {
        val vm = createViewModel(genreId = 1L, classicalGenreId = null)
        assertFalse(vm.isClassical)
    }
}
