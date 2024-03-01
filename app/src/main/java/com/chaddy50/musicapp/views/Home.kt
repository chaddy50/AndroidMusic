package com.chaddy50.musicapp.views

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.chaddy50.musicapp.components.EntityCard
import com.chaddy50.musicapp.components.TopBar
import com.chaddy50.musicapp.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(
    navController: NavController
) {
    Scaffold(
        topBar = { TopBar("Home") }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            if (ActivityCompat.checkSelfPermission(LocalContext.current, Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_DENIED) {
                Text("Go to settings to give this app permission to access your audio files.")
            }
            else {
                val views = listOf("Genre", "Album", "Artist", "Track")
                views.forEach {title ->
                    val screenRoute = getScreenRouteFromTitle(title)
                    EntityCard(title) { navController.navigate(screenRoute) }
                }
            }
        }
    }
}

private fun getScreenRouteFromTitle(title: String): String {
    return when (title) {
        "Genre" -> Screen.GenreScreen.route
        "Album" -> Screen.AlbumScreen.route
        "Artist" -> Screen.ArtistScreen.route
        "Track" -> Screen.TrackScreen.route
        else -> ""
    }
}