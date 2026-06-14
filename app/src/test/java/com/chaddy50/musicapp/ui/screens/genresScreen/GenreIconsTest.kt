package com.chaddy50.musicapp.ui.screens.genresScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Piano
import org.junit.Assert.assertEquals
import org.junit.Test

class GenreIconsTest {

    @Test
    fun genreIconReturnsSpecificIconForKnownGenre() {
        assertEquals(Icons.Filled.Piano, genreIcon("Classical"))
        assertEquals(Icons.Filled.ElectricBolt, genreIcon("Rock"))
    }

    @Test
    fun genreIconReturnsDefaultForUnknownGenre() {
        assertEquals(Icons.Filled.LibraryMusic, genreIcon("Polka"))
        assertEquals(Icons.Filled.LibraryMusic, genreIcon("Reggae"))
    }

    @Test
    fun genreIconIsCaseInsensitive() {
        assertEquals(genreIcon("Classical"), genreIcon("classical"))
        assertEquals(genreIcon("Classical"), genreIcon("CLASSICAL"))
    }
}
