package com.chaddy50.musicapp.ui.composables

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EntityScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun showsFabsWhenBothCallbacksProvided() {
        composeTestRule.setContent {
            EntityScreen(
                isLoading = false,
                content = {},
                onPlay = {},
                onShuffle = {},
            )
        }
        composeTestRule.onNodeWithContentDescription("Play").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Shuffle").assertIsDisplayed()
    }

    @Test
    fun hidesFabsWhenBothCallbacksNull() {
        composeTestRule.setContent {
            EntityScreen(
                isLoading = false,
                content = {},
                onPlay = null,
                onShuffle = null,
            )
        }
        composeTestRule.onNodeWithContentDescription("Play").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Shuffle").assertDoesNotExist()
    }

    @Test
    fun hidesFabsWhenOnlyPlayNull() {
        composeTestRule.setContent {
            EntityScreen(
                isLoading = false,
                content = {},
                onPlay = null,
                onShuffle = {},
            )
        }
        composeTestRule.onNodeWithContentDescription("Play").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Shuffle").assertDoesNotExist()
    }

    @Test
    fun hidesFabsWhenOnlyShuffleNull() {
        composeTestRule.setContent {
            EntityScreen(
                isLoading = false,
                content = {},
                onPlay = {},
                onShuffle = null,
            )
        }
        composeTestRule.onNodeWithContentDescription("Play").assertDoesNotExist()
        composeTestRule.onNodeWithContentDescription("Shuffle").assertDoesNotExist()
    }
}
