package com.chaddy50.musicapp.views

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
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
            var views = listOf("Genre", "Album","Artist","Track")
            views.forEach {
                Card (
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .align(Alignment.CenterHorizontally),
                    onClick = {
                        val screenRoute = getScreenRouteFromTitle(it)
                        navController.navigate(screenRoute)
                    }
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(it)
                    }
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