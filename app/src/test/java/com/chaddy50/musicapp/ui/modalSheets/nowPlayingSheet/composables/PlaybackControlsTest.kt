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
class PlaybackControlsTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun displaysSkipPreviousButton() {
        composeTestRule.setContent {
            PlaybackControls(
                isPlaying = false,
                onPlayPause = {},
                onSkipToPreviousTrack = {},
                onSkipToNextTrack = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Skip Previous").assertIsDisplayed()
    }

    @Test
    fun displaysPlayButtonWhenNotPlaying() {
        composeTestRule.setContent {
            PlaybackControls(
                isPlaying = false,
                onPlayPause = {},
                onSkipToPreviousTrack = {},
                onSkipToNextTrack = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Play").assertIsDisplayed()
    }

    @Test
    fun displaysPauseButtonWhenPlaying() {
        composeTestRule.setContent {
            PlaybackControls(
                isPlaying = true,
                onPlayPause = {},
                onSkipToPreviousTrack = {},
                onSkipToNextTrack = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Pause").assertIsDisplayed()
    }

    @Test
    fun displaysSkipNextButton() {
        composeTestRule.setContent {
            PlaybackControls(
                isPlaying = false,
                onPlayPause = {},
                onSkipToPreviousTrack = {},
                onSkipToNextTrack = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Skip Next").assertIsDisplayed()
    }

    @Test
    fun doesNotDisplayShuffleButton() {
        composeTestRule.setContent {
            PlaybackControls(
                isPlaying = false,
                onPlayPause = {},
                onSkipToPreviousTrack = {},
                onSkipToNextTrack = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Shuffle").assertDoesNotExist()
    }

    @Test
    fun doesNotDisplayScrobbleButton() {
        composeTestRule.setContent {
            PlaybackControls(
                isPlaying = false,
                onPlayPause = {},
                onSkipToPreviousTrack = {},
                onSkipToNextTrack = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Toggle Scrobbling").assertDoesNotExist()
    }

    @Test
    fun clickingPlayPauseTriggersCallback() {
        var clicked = false
        composeTestRule.setContent {
            PlaybackControls(
                isPlaying = false,
                onPlayPause = { clicked = true },
                onSkipToPreviousTrack = {},
                onSkipToNextTrack = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Play").performClick()
        assert(clicked)
    }

    @Test
    fun clickingSkipPreviousTriggersCallback() {
        var clicked = false
        composeTestRule.setContent {
            PlaybackControls(
                isPlaying = false,
                onPlayPause = {},
                onSkipToPreviousTrack = { clicked = true },
                onSkipToNextTrack = {}
            )
        }
        composeTestRule.onNodeWithContentDescription("Skip Previous").performClick()
        assert(clicked)
    }

    @Test
    fun clickingSkipNextTriggersCallback() {
        var clicked = false
        composeTestRule.setContent {
            PlaybackControls(
                isPlaying = false,
                onPlayPause = {},
                onSkipToPreviousTrack = {},
                onSkipToNextTrack = { clicked = true }
            )
        }
        composeTestRule.onNodeWithContentDescription("Skip Next").performClick()
        assert(clicked)
    }
}
