package com.chaddy50.musicapp.utilities

import org.junit.Assert.assertEquals
import org.junit.Test

class ParseTrackNumberTest {

    @Test
    fun nullReturnsNegativeOne() {
        assertEquals(-1, parseTrackNumber(null))
    }

    @Test
    fun simpleNumber() {
        assertEquals(5, parseTrackNumber("5"))
    }

    @Test
    fun slashFormatReturnsTrackNumber() {
        assertEquals(3, parseTrackNumber("3/12"))
    }

    @Test
    fun slashFormatWithLargeNumbers() {
        assertEquals(14, parseTrackNumber("14/28"))
    }

    @Test
    fun nonNumericReturnsNegativeOne() {
        assertEquals(-1, parseTrackNumber("abc"))
    }

    @Test
    fun emptyStringReturnsNegativeOne() {
        assertEquals(-1, parseTrackNumber(""))
    }

    @Test
    fun slashWithNonNumericBeforeItReturnsNegativeOne() {
        assertEquals(-1, parseTrackNumber("abc/12"))
    }

    @Test
    fun singleTrackAlbum() {
        assertEquals(1, parseTrackNumber("1/1"))
    }
}