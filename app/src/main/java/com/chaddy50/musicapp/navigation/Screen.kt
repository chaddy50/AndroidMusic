package com.chaddy50.musicapp.navigation

sealed class Screen (
    val route: String
){
    object HomeScreen: Screen("home_screen")
    object GenreScreen: Screen("genre_screen")
    object AlbumScreen: Screen("album_screen")
    object ArtistScreen: Screen("artist_screen")
    object TrackScreen: Screen("track_screen")
}