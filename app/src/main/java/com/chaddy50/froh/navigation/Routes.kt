package com.chaddy50.froh.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data class ArtistsRoute(val genreId: Long, val title: String)

@Serializable
data class AlbumsRoute(val genreId: Long, val albumArtistId: Long, val title: String)

@Serializable
data class PerformancesRoute(val genreId: Long, val albumId: Long, val title: String)

@Serializable
data class TracksRoute(val genreId: Long, val albumId: Long, val performanceId: Long = -1L, val title: String)

@Serializable
data class PlaylistTracksRoute(val playlistId: Long, val title: String)

@Serializable
data object SettingsRoute

@Serializable
data object ClassicalGenreSettingsRoute
