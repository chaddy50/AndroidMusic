package com.chaddy50.musicapp.ui.modalSheets.nowPlayingSheet.composables

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PagerIndicatorTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun displaysCorrectNumberOfDotsForTwoPages() {
        composeTestRule.setContent {
            PagerIndicator(pageCount = 2, currentPage = 0)
        }
        val activeDots = composeTestRule.onAllNodesWithTag("pager_dot_active").fetchSemanticsNodes().size
        val inactiveDots = composeTestRule.onAllNodesWithTag("pager_dot_inactive").fetchSemanticsNodes().size
        assert(activeDots + inactiveDots == 2)
    }

    @Test
    fun displaysCorrectNumberOfDotsForThreePages() {
        composeTestRule.setContent {
            PagerIndicator(pageCount = 3, currentPage = 0)
        }
        val activeDots = composeTestRule.onAllNodesWithTag("pager_dot_active").fetchSemanticsNodes().size
        val inactiveDots = composeTestRule.onAllNodesWithTag("pager_dot_inactive").fetchSemanticsNodes().size
        assert(activeDots + inactiveDots == 3)
    }

    @Test
    fun singlePageDisplaysOneDot() {
        composeTestRule.setContent {
            PagerIndicator(pageCount = 1, currentPage = 0)
        }
        val activeDots = composeTestRule.onAllNodesWithTag("pager_dot_active").fetchSemanticsNodes().size
        assert(activeDots == 1)
    }

    @Test
    fun firstDotIsActiveWhenCurrentPageIsZero() {
        composeTestRule.setContent {
            PagerIndicator(pageCount = 2, currentPage = 0)
        }
        val activeDots = composeTestRule.onAllNodesWithTag("pager_dot_active").fetchSemanticsNodes().size
        val inactiveDots = composeTestRule.onAllNodesWithTag("pager_dot_inactive").fetchSemanticsNodes().size
        assert(activeDots == 1)
        assert(inactiveDots == 1)
    }

    @Test
    fun secondDotIsActiveWhenCurrentPageIsOne() {
        composeTestRule.setContent {
            PagerIndicator(pageCount = 2, currentPage = 1)
        }
        val activeDots = composeTestRule.onAllNodesWithTag("pager_dot_active").fetchSemanticsNodes().size
        val inactiveDots = composeTestRule.onAllNodesWithTag("pager_dot_inactive").fetchSemanticsNodes().size
        assert(activeDots == 1)
        assert(inactiveDots == 1)
    }
}
