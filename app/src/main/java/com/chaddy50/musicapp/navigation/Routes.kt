package com.chaddy50.musicapp.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data class ArtistsRoute(val genreId: Long)

@Serializable
data class AlbumsRoute(val genreId: Long, val albumArtistId: Long)

@Serializable
data class PerformancesRoute(val genreId: Long, val albumId: Long)

@Serializable
data class TracksRoute(val genreId: Long, val albumId: Long, val performanceId: Long = -1L)

@Serializable
data class PlaylistTracksRoute(val playlistId: Long)
