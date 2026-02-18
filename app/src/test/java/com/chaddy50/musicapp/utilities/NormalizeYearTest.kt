package com.chaddy50.musicapp.utilities

import org.junit.Assert.assertEquals
import org.junit.Test

class NormalizeYearTest {

    @Test
    fun nullReturnsUnknownYear() {
        assertEquals("Unknown Year", normalizeYear(null))
    }

    @Test
    fun blankReturnsUnknownYear() {
        assertEquals("Unknown Year", normalizeYear(""))
        assertEquals("Unknown Year", normalizeYear("   "))
    }

    @Test
    fun zeroReturnsUnknownYear() {
        assertEquals("Unknown Year", normalizeYear("0"))
    }

    @Test
    fun fourDigitYearUnchanged() {
        assertEquals("2023", normalizeYear("2023"))
    }

    @Test
    fun longDateStringTruncatedToFourChars() {
        assertEquals("2023", normalizeYear("20230101"))
        assertEquals("2024", normalizeYear("2024-06-15"))
    }

    @Test
    fun shortYearUnchanged() {
        assertEquals("99", normalizeYear("99"))
    }
}