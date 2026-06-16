package com.chaddy50.musicapp.ui.screens.playlistsScreen

import com.chaddy50.musicapp.data.entity.Playlist
import com.chaddy50.musicapp.data.repository.PlaylistRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import com.chaddy50.musicapp.fakes.FakePlaylistDao
import com.chaddy50.musicapp.fakes.FakeTrackDao
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
class PlaylistsScreenViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val playlistsFlow = MutableStateFlow<List<Playlist>>(emptyList())

    private fun createViewModel(): PlaylistsScreenViewModel {
        val dao = FakePlaylistDao(playlistsFlow)
        return PlaylistsScreenViewModel(PlaylistRepository(dao), TrackRepository(FakeTrackDao()))
    }

    @Test
    fun initialStateIsLoading() {
        val vm = createViewModel()
        assertTrue(vm.uiState.value.isLoading)
        assertEquals(emptyList<Playlist>(), vm.uiState.value.playlists)
    }

    @Test
    fun emitsPlaylistsFromRepository() = runTest {
        val vm = createViewModel()
        backgroundScope.launch { vm.uiState.collect() }
        val playlists = listOf(
            Playlist(id = 1, name = "Favorites"),
            Playlist(id = 2, name = "Workout"),
        )
        playlistsFlow.value = playlists
        advanceUntilIdle()
        assertFalse(vm.uiState.value.isLoading)
        assertEquals(playlists, vm.uiState.value.playlists)
    }

    @Test
    fun updatesWhenRepositoryEmitsNewList() = runTest {
        val vm = createViewModel()
        backgroundScope.launch { vm.uiState.collect() }
        playlistsFlow.value = listOf(Playlist(id = 1, name = "Favorites"))
        advanceUntilIdle()
        assertEquals(1, vm.uiState.value.playlists.size)

        playlistsFlow.value = listOf(
            Playlist(id = 1, name = "Favorites"),
            Playlist(id = 2, name = "Workout"),
        )
        advanceUntilIdle()
        assertEquals(2, vm.uiState.value.playlists.size)
    }
}
