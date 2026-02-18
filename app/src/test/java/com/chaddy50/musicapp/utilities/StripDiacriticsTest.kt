package com.chaddy50.musicapp.utilities

import org.junit.Assert.assertEquals
import org.junit.Test

class StripDiacriticsTest {

    @Test
    fun removesAccuteAccent() {
        assertEquals("Dvorak", stripDiacritics("Dvořák"))
    }

    @Test
    fun removesUmlaut() {
        assertEquals("Handel", stripDiacritics("Händel"))
    }

    @Test
    fun removesTrema() {
        assertEquals("Saint-Saens", stripDiacritics("Saint-Saëns"))
    }

    @Test
    fun removesCedilla() {
        assertEquals("Francais", stripDiacritics("Français"))
    }

    @Test
    fun removesMultipleDiacritics() {
        assertEquals("Bela Bartok", stripDiacritics("Béla Bartók"))
    }

    @Test
    fun asciiUnchanged() {
        assertEquals("Beethoven", stripDiacritics("Beethoven"))
    }

    @Test
    fun emptyString() {
        assertEquals("", stripDiacritics(""))
    }

    @Test
    fun preservesNonLatinCharacters() {
        assertEquals("Bach 巴赫", stripDiacritics("Bach 巴赫"))
    }
}