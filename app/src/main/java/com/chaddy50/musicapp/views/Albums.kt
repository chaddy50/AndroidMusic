package com.chaddy50.musicapp.views

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.database.getStringOrNull
import androidx.navigation.NavController
import com.chaddy50.musicapp.components.EntityCard
import com.chaddy50.musicapp.components.TopBar
import com.chaddy50.musicapp.data.Album
import com.chaddy50.musicapp.data.Artist
import com.chaddy50.musicapp.data.MusicDatabase
import com.chaddy50.musicapp.navigation.Screen
import java.sql.Date

@Composable
fun Albums(
    context: Context,
    musicDatabase: MusicDatabase,
    artistID: Int = 0
) {
    LazyColumn {
        var albumsToShow = musicDatabase.albums.toList()
        if (artistID != 0) {
            albumsToShow = musicDatabase.albums.filter { album -> album.artistID == artistID }
        }
        items(albumsToShow) { album ->
            EntityCard(
                album.title,
                //{ navController.navigate(Screen.TrackScreen.route + "?albumID=${album.id}") }
            )
        }
    }
}