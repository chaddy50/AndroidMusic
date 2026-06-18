package com.chaddy50.musicapp.ui.screens.settingsScreen.genreMappings

import com.chaddy50.musicapp.data.entity.Genre
import com.chaddy50.musicapp.data.entity.GenreMapping
import com.chaddy50.musicapp.data.repository.GenreMappingRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.fakes.FakeGenreDao
import com.chaddy50.musicapp.fakes.FakeGenreMappingDao
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

@OptIn(ExperimentalCoroutinesApi::class)
class ClassicalGenreSettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val allGenresFlow = MutableStateFlow<List<Genre>>(emptyList())

    private fun createViewModel(
        genres: List<Genre> = emptyList(),
        classicalNames: List<String> = emptyList(),
    ): ClassicalGenreSettingsViewModel {
        allGenresFlow.value = genres
        val genreDao = FakeGenreDao(allGenres = allGenresFlow)
        val mappingDao = FakeGenreMappingDao()
        mappingDao.mappings.addAll(
            classicalNames.map { GenreMapping(subGenreName = it, parentGenreName = "Classical") }
        )
        return ClassicalGenreSettingsViewModel(
            GenreRepository(genreDao),
            GenreMappingRepository(mappingDao),
        )
    }

    @Test
    fun initialStateIsLoading() = runTest {
        val vm = createViewModel()
        assertTrue(vm.uiState.value.isLoading)
    }

    @Test
    fun emitsGenreListFromRepository() = runTest {
        val genres = listOf(
            Genre(1L, "Rock"),
            Genre(2L, "Jazz"),
            Genre(3L, "Symphony", parentGenreId = 4L),
        )
        val vm = createViewModel(genres = genres)
        val job = launch { vm.uiState.collect() }
        advanceUntilIdle()

        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals(3, state.genres.size)
        assertEquals("Rock", state.genres[0].genreName)
        assertEquals("Jazz", state.genres[1].genreName)
        assertEquals("Symphony", state.genres[2].genreName)

        job.cancel()
    }

    @Test
    fun preSelectsClassicalGenres() = runTest {
        val genres = listOf(
            Genre(1L, "Rock"),
            Genre(2L, "Symphony"),
            Genre(3L, "Solo Piano"),
        )
        val vm = createViewModel(genres = genres, classicalNames = listOf("Symphony", "Solo Piano"))
        val job = launch { vm.uiState.collect() }
        advanceUntilIdle()

        val state = vm.uiState.value
        assertFalse(state.genres[0].isSelected) // Rock
        assertTrue(state.genres[1].isSelected)  // Symphony
        assertTrue(state.genres[2].isSelected)  // Solo Piano

        job.cancel()
    }

    @Test
    fun togglesGenreSelection() = runTest {
        val genres = listOf(Genre(1L, "Rock"), Genre(2L, "Symphony"))
        val vm = createViewModel(genres = genres, classicalNames = listOf("Symphony"))
        val job = launch { vm.uiState.collect() }
        advanceUntilIdle()

        // Toggle Rock on
        vm.toggleGenreSelection("Rock")
        advanceUntilIdle()
        assertTrue(vm.uiState.value.genres[0].isSelected)

        // Toggle Symphony off
        vm.toggleGenreSelection("Symphony")
        advanceUntilIdle()
        assertFalse(vm.uiState.value.genres[1].isSelected)

        job.cancel()
    }

    @Test
    fun toggleDoesNotAffectOtherGenres() = runTest {
        val genres = listOf(Genre(1L, "Rock"), Genre(2L, "Jazz"))
        val vm = createViewModel(genres = genres)
        val job = launch { vm.uiState.collect() }
        advanceUntilIdle()

        vm.toggleGenreSelection("Rock")
        advanceUntilIdle()

        assertTrue(vm.uiState.value.genres[0].isSelected) // Rock toggled
        assertFalse(vm.uiState.value.genres[1].isSelected) // Jazz unchanged

        job.cancel()
    }

    @Test
    fun saveReturnsTrueWhenSelectionsChanged() = runTest {
        val genres = listOf(Genre(1L, "Rock"), Genre(2L, "Symphony"))
        val vm = createViewModel(genres = genres, classicalNames = listOf("Symphony"))
        val job = launch { vm.uiState.collect() }
        advanceUntilIdle()

        vm.toggleGenreSelection("Rock")
        advanceUntilIdle()
        assertTrue(vm.saveSelections())

        job.cancel()
    }

    @Test
    fun saveReturnsFalseWhenSelectionsUnchanged() = runTest {
        val genres = listOf(Genre(1L, "Rock"), Genre(2L, "Symphony"))
        val vm = createViewModel(genres = genres, classicalNames = listOf("Symphony"))
        val job = launch { vm.uiState.collect() }
        advanceUntilIdle()

        assertFalse(vm.saveSelections())

        job.cancel()
    }

    @Test
    fun saveReturnsFalseAfterToggleAndUntoggle() = runTest {
        val genres = listOf(Genre(1L, "Rock"))
        val vm = createViewModel(genres = genres)
        val job = launch { vm.uiState.collect() }
        advanceUntilIdle()

        vm.toggleGenreSelection("Rock")
        vm.toggleGenreSelection("Rock") // back to original
        advanceUntilIdle()
        assertFalse(vm.saveSelections())

        job.cancel()
    }

    @Test
    fun savePersistsSelectionsToRepository() = runTest {
        val genres = listOf(Genre(1L, "Rock"), Genre(2L, "Symphony"))
        val mappingDao = FakeGenreMappingDao()
        allGenresFlow.value = genres
        val genreDao = FakeGenreDao(allGenres = allGenresFlow)
        val mappingRepo = GenreMappingRepository(mappingDao)
        val vm = ClassicalGenreSettingsViewModel(GenreRepository(genreDao), mappingRepo)
        val job = launch { vm.uiState.collect() }
        advanceUntilIdle()

        vm.toggleGenreSelection("Rock")
        vm.toggleGenreSelection("Symphony")
        advanceUntilIdle()
        vm.saveSelections()

        val saved = mappingRepo.getClassicalGenreNames()
        assertEquals(setOf("Rock", "Symphony"), saved)

        job.cancel()
    }
}
