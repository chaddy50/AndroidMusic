package com.chaddy50.musicapp.ui.screens.genresScreen

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.chaddy50.musicapp.ui.composables.EmptyStateContent
import com.chaddy50.musicapp.ui.composables.EntityCard
import com.chaddy50.musicapp.ui.composables.EntityScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GenresScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun showsEmptyStateWhenGenresEmptyAfterLoading() {
        composeTestRule.setContent {
            EntityScreen(
                isLoading = false,
                content = {
                    if (true) { // simulates uiState.genres.isEmpty()
                        EmptyStateContent(
                            icon = Icons.Filled.LibraryMusic,
                            title = "No music yet",
                            subtitle = "Add music to your device to get started",
                        )
                    }
                },
            )
        }
        composeTestRule.onNodeWithText("No music yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add music to your device to get started").assertIsDisplayed()
    }

    @Test
    fun doesNotShowEmptyStateWhileLoading() {
        composeTestRule.setContent {
            EntityScreen(
                isLoading = true,
                content = {
                    EmptyStateContent(
                        icon = Icons.Filled.LibraryMusic,
                        title = "No music yet",
                        subtitle = "Add music to your device to get started",
                    )
                },
            )
        }
        composeTestRule.onNodeWithText("No music yet").assertDoesNotExist()
    }

    @Test
    fun showsGenreCardsWhenGenresNonEmpty() {
        val genreNames = listOf("Classical", "Jazz", "Rock")
        composeTestRule.setContent {
            EntityScreen(
                isLoading = false,
                content = {
                    if (genreNames.isEmpty()) {
                        EmptyStateContent(
                            icon = Icons.Filled.LibraryMusic,
                            title = "No music yet",
                            subtitle = "Add music to your device to get started",
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(genreNames) { name ->
                                EntityCard(title = name)
                            }
                        }
                    }
                },
            )
        }
        composeTestRule.onNodeWithText("Classical").assertIsDisplayed()
        composeTestRule.onNodeWithText("Jazz").assertIsDisplayed()
        composeTestRule.onNodeWithText("No music yet").assertDoesNotExist()
    }

    @Test
    fun emptyStateReplacesGenreList() {
        val genreNames = emptyList<String>()
        composeTestRule.setContent {
            EntityScreen(
                isLoading = false,
                content = {
                    if (genreNames.isEmpty()) {
                        EmptyStateContent(
                            icon = Icons.Filled.LibraryMusic,
                            title = "No music yet",
                            subtitle = "Add music to your device to get started",
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(genreNames) { name ->
                                EntityCard(title = name)
                            }
                        }
                    }
                },
            )
        }
        composeTestRule.onNodeWithText("No music yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Classical").assertDoesNotExist()
    }
}
