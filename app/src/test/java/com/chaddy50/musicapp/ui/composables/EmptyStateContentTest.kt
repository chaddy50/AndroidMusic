package com.chaddy50.musicapp.ui.composables

import androidx.activity.ComponentActivity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EmptyStateContentTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun displaysTitle() {
        composeTestRule.setContent {
            EmptyStateContent(
                icon = Icons.Filled.MusicNote,
                title = "No music yet",
                subtitle = "Add music to your device to get started",
            )
        }
        composeTestRule.onNodeWithText("No music yet").assertIsDisplayed()
    }

    @Test
    fun displaysSubtitle() {
        composeTestRule.setContent {
            EmptyStateContent(
                icon = Icons.Filled.MusicNote,
                title = "No music yet",
                subtitle = "Add music to your device to get started",
            )
        }
        composeTestRule.onNodeWithText("Add music to your device to get started").assertIsDisplayed()
    }

    @Test
    fun titleAndSubtitleBothVisible() {
        composeTestRule.setContent {
            EmptyStateContent(
                icon = Icons.Filled.MusicNote,
                title = "No music yet",
                subtitle = "Add music to your device to get started",
            )
        }
        composeTestRule.onNodeWithText("No music yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add music to your device to get started").assertIsDisplayed()
    }

    @Test
    fun rendersWithEmptyTitle() {
        composeTestRule.setContent {
            EmptyStateContent(
                icon = Icons.Filled.MusicNote,
                title = "",
                subtitle = "Some subtitle",
            )
        }
        composeTestRule.onNodeWithText("Some subtitle").assertIsDisplayed()
    }

    @Test
    fun rendersWithEmptySubtitle() {
        composeTestRule.setContent {
            EmptyStateContent(
                icon = Icons.Filled.MusicNote,
                title = "Some title",
                subtitle = "",
            )
        }
        composeTestRule.onNodeWithText("Some title").assertIsDisplayed()
    }

    @Test
    fun rendersWithDifferentIcons() {
        composeTestRule.setContent {
            EmptyStateContent(
                icon = Icons.AutoMirrored.Filled.PlaylistAdd,
                title = "No playlists yet",
                subtitle = "Tap + to create your first playlist",
            )
        }
        composeTestRule.onNodeWithText("No playlists yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Tap + to create your first playlist").assertIsDisplayed()
    }
}
