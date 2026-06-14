package com.chaddy50.musicapp.ui.screens.genresScreen

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Nightlife
import androidx.compose.material.icons.filled.Piano
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TheaterComedy
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.ui.graphics.vector.ImageVector

private val genreIconMap = mapOf(
    "ambient" to Icons.Filled.Air,
    "anime" to Icons.Filled.Star,
    "blues" to Icons.Filled.WaterDrop,
    "classical" to Icons.Filled.Piano,
    "country" to Icons.Filled.Forest,
    "electronic" to Icons.Filled.Equalizer,
    "emo" to Icons.Filled.SentimentVeryDissatisfied,
    "folk" to Icons.Filled.Forest,
    "hip hop" to Icons.Filled.Mic,
    "jazz" to Icons.Filled.Nightlife,
    "metal" to Icons.Filled.Whatshot,
    "movie" to Icons.Filled.Movie,
    "musical" to Icons.Filled.TheaterComedy,
    "pop" to Icons.Filled.Star,
    "r&b" to Icons.Filled.Favorite,
    "rap" to Icons.Filled.Mic,
    "rock" to Icons.Filled.ElectricBolt,
    "soul" to Icons.Filled.Favorite,
    "video game" to Icons.Filled.Gamepad,
)

fun genreIcon(genreName: String): ImageVector {
    return genreIconMap[genreName.lowercase()] ?: Icons.Filled.LibraryMusic
}
