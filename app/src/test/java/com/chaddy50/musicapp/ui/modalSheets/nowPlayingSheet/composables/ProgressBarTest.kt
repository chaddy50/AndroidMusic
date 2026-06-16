package com.chaddy50.musicapp.ui.modalSheets.nowPlayingSheet.composables

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ProgressBarTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private fun setContent(
        playbackPosition: Long = 0L,
        durationMs: Long = 180000L,
        onSeek: (Long) -> Unit = {}
    ) {
        composeTestRule.setContent {
            ProgressBar(
                playbackPosition = playbackPosition,
                durationMs = durationMs,
                onSeek = onSeek
            )
        }
    }

    @Test
    fun displaysElapsedTime() {
        setContent(playbackPosition = 30000L, durationMs = 180000L)
        composeTestRule.onNodeWithText("0:30").assertIsDisplayed()
    }

    @Test
    fun displaysTotalDuration() {
        setContent(playbackPosition = 0L, durationMs = 180000L)
        composeTestRule.onNodeWithText("3:00").assertIsDisplayed()
    }

    @Test
    fun displaysZeroElapsedTimeAtStart() {
        setContent(playbackPosition = 0L, durationMs = 180000L)
        composeTestRule.onNodeWithText("0:00").assertIsDisplayed()
    }

    @Test
    fun handlesZeroDuration() {
        setContent(playbackPosition = 0L, durationMs = 0L)
        // Both elapsed and total should show "0:00"
        val nodes = composeTestRule.onAllNodesWithText("0:00")
        assert(nodes.fetchSemanticsNodes().size == 2)
    }

    @Test
    fun displaysSlider() {
        setContent()
        // Verify the composable renders without crashing and contains time labels
        composeTestRule.onNodeWithText("0:00").assertIsDisplayed()
        composeTestRule.onNodeWithText("3:00").assertIsDisplayed()
    }
}
