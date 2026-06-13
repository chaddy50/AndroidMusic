package com.chaddy50.musicapp.ui.composables

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EntityCardTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun displaysTitle() {
        composeTestRule.setContent {
            EntityCard(title = "Glenn Gould", subtitle = "1981")
        }
        composeTestRule.onNodeWithText("Glenn Gould").assertIsDisplayed()
    }

    @Test
    fun displaysSubtitle() {
        composeTestRule.setContent {
            EntityCard(title = "Glenn Gould", subtitle = "1981")
        }
        composeTestRule.onNodeWithText("1981").assertIsDisplayed()
    }

    @Test
    fun titleAndSubtitleBothVisible() {
        composeTestRule.setContent {
            EntityCard(title = "Glenn Gould", subtitle = "1981")
        }
        composeTestRule.onNodeWithText("Glenn Gould").assertIsDisplayed()
        composeTestRule.onNodeWithText("1981").assertIsDisplayed()
    }

    @Test
    fun subtitleHiddenWhenNull() {
        composeTestRule.setContent {
            EntityCard(title = "Glenn Gould")
        }
        composeTestRule.onNodeWithText("Glenn Gould").assertIsDisplayed()
    }

    @Test
    fun displaysCatalogueNumberAsSubtitle() {
        composeTestRule.setContent {
            EntityCard(title = "Goldberg Variations", subtitle = "988")
        }
        composeTestRule.onNodeWithText("Goldberg Variations").assertIsDisplayed()
        composeTestRule.onNodeWithText("988").assertIsDisplayed()
    }
}
