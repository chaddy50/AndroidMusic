package com.chaddy50.musicapp.ui.modalSheets.nowPlayingSheet

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class GetContrastingColorTest {

    @Test
    fun returnsBlackForHighLuminanceBackground() {
        assertEquals(Color.Black, getContrastingColor(Color.White))
        assertEquals(Color.Black, getContrastingColor(Color.Yellow))
    }

    @Test
    fun returnsWhiteForLowLuminanceBackground() {
        assertEquals(Color.White, getContrastingColor(Color.Black))
        assertEquals(Color.White, getContrastingColor(Color(0xFF003300)))
    }

    @Test
    fun handlesThresholdBoundary() {
        // Luminance just above 0.179 → Black
        val aboveThreshold = Color(0xFF808080) // mid-gray, luminance ~0.216
        assertEquals(Color.Black, getContrastingColor(aboveThreshold))

        // Luminance just below 0.179 → White
        val belowThreshold = Color(0xFF5A5A5A) // darker gray, luminance ~0.087
        assertEquals(Color.White, getContrastingColor(belowThreshold))
    }
}
