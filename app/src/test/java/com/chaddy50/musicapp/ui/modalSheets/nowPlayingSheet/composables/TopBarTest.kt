package com.chaddy50.musicapp.ui.modalSheets.nowPlayingSheet.composables

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TopBarTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun setContent(
        isShowingQueue: Boolean = false,
        isShuffleModeEnabled: Boolean = false,
        onDismiss: () -> Unit = {},
        onQueueToggled: () -> Unit = {},
        onShuffleToggled: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            TopBar(
                onDismiss = onDismiss,
                isShowingQueue = isShowingQueue,
                onQueueToggled = onQueueToggled,
                isShuffleModeEnabled = isShuffleModeEnabled,
                onShuffleToggled = onShuffleToggled
            )
        }
    }

    @Test
    fun displaysDismissButton() {
        setContent()
        composeTestRule.onNodeWithContentDescription("Close").assertIsDisplayed()
    }

    @Test
    fun displaysQueueButton() {
        setContent()
        composeTestRule.onNodeWithContentDescription("Queue").assertIsDisplayed()
    }

    @Test
    fun displaysShuffleButton() {
        setContent()
        composeTestRule.onNodeWithContentDescription("Shuffle").assertIsDisplayed()
    }

    @Test
    fun displaysShuffleButtonWhenEnabled() {
        setContent(isShuffleModeEnabled = true)
        composeTestRule.onNodeWithContentDescription("Shuffle").assertIsDisplayed()
    }

    @Test
    fun clickingDismissTriggersCallback() {
        var clicked = false
        setContent(onDismiss = { clicked = true })
        composeTestRule.onNodeWithContentDescription("Close").performClick()
        assert(clicked)
    }

    @Test
    fun clickingQueueTriggersCallback() {
        var clicked = false
        setContent(onQueueToggled = { clicked = true })
        composeTestRule.onNodeWithContentDescription("Queue").performClick()
        assert(clicked)
    }

    @Test
    fun clickingShuffleTriggersCallback() {
        var clicked = false
        setContent(onShuffleToggled = { clicked = true })
        composeTestRule.onNodeWithContentDescription("Shuffle").performClick()
        assert(clicked)
    }
}
