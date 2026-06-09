package com.chaddy50.musicapp.ui.screens.genresScreen

import com.chaddy50.musicapp.data.entity.Genre
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.fakes.FakeGenreDao
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
class GenresScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val genresFlow = MutableStateFlow<List<Genre>>(emptyList())

    private fun createViewModel(): GenresScreenViewModel {
        val dao = FakeGenreDao(genresFlow)
        return GenresScreenViewModel(GenreRepository(dao))
    }

    @Test
    fun initialStateIsLoading() {
        val vm = createViewModel()
        val state = vm.uiState.value
        assertTrue(state.isLoading)
        assertEquals(emptyList<Genre>(), state.genres)
    }

    @Test
    fun emitsGenresFromRepository() = runTest {
        val vm = createViewModel()
        val collectJob = backgroundScope.launch { vm.uiState.collect() }
        val genres = listOf(
            Genre(id = 1, name = "Classical"),
            Genre(id = 2, name = "Rock"),
        )
        genresFlow.value = genres
        advanceUntilIdle()
        val state = vm.uiState.value
        assertFalse(state.isLoading)
        assertEquals(genres, state.genres)
    }

    @Test
    fun screenTitleIsGenres() = runTest {
        val vm = createViewModel()
        val collectJob = backgroundScope.launch { vm.uiState.collect() }
        genresFlow.value = emptyList()
        advanceUntilIdle()
        assertEquals("Genres", vm.uiState.value.screenTitle)
    }

    @Test
    fun updatesWhenRepositoryEmitsNewList() = runTest {
        val vm = createViewModel()
        val collectJob = backgroundScope.launch { vm.uiState.collect() }
        genresFlow.value = listOf(Genre(id = 1, name = "Rock"))
        advanceUntilIdle()
        assertEquals(1, vm.uiState.value.genres.size)

        genresFlow.value = listOf(
            Genre(id = 1, name = "Rock"),
            Genre(id = 2, name = "Jazz"),
        )
        advanceUntilIdle()
        assertEquals(2, vm.uiState.value.genres.size)
    }
}
