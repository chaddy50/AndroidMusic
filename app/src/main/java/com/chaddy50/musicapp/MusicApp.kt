package com.chaddy50.musicapp

import android.Manifest
import android.app.Application
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.chaddy50.musicapp.data.MusicDatabase
import com.chaddy50.musicapp.data.MusicScanner
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.AlbumRepository
import com.chaddy50.musicapp.data.repository.ArtistRepository
import com.chaddy50.musicapp.data.repository.GenreMappingRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.repository.PerformanceRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import com.chaddy50.musicapp.navigation.NavigationHost
import com.chaddy50.musicapp.ui.theme.MusicAppTheme
import com.chaddy50.musicapp.viewModel.MusicAppViewModel
import com.chaddy50.musicapp.viewModel.MusicAppViewModelFactory
import kotlinx.coroutines.launch

class MusicApp : ComponentActivity() {
    private val viewModel: MusicAppViewModel by viewModels {
        MusicAppViewModelFactory(application as MusicApplication)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationHost(viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.READ_MEDIA_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //triggerLibraryScanIfNeeded()
        } else {
            permissionRequestLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
        }
    }

    private val permissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { hasPermission ->
            if (hasPermission) {
                triggerLibraryScanIfNeeded()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_MEDIA_AUDIO
                    )
                ) {
                    Toast.makeText(
                        this.applicationContext,
                        "This app needs access to read your audio files",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this.applicationContext,
                        "Go to settings to give this app read access to your audio files",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

    private fun triggerLibraryScanIfNeeded() {
        lifecycleScope.launch {
            val trackCount = viewModel.getTrackCount()

            if (trackCount == 0) {
                viewModel.refreshLibrary()
            }
        }
    }
}

class MusicApplication: Application() {
    val database: MusicDatabase by lazy {
        MusicDatabase.getDatabase(this)
    }

    val trackRepository by lazy { TrackRepository(database.trackDao()) }
    val albumRepository by lazy { AlbumRepository(database.albumDao()) }
    val artistRepository by lazy { ArtistRepository(database.artistDao()) }
    val genreRepository by lazy { GenreRepository(database.genreDao()) }
    val albumArtistRepository by lazy { AlbumArtistRepository(database.albumArtistDao(), database.genreDao()) }
    val genreMappingRepository by lazy { GenreMappingRepository(database.genreMappingDao()) }
    val performanceRepository by lazy { PerformanceRepository(database.performanceDao()) }

    val musicScanner by lazy {
        MusicScanner(
            this,
            genreRepository,
            artistRepository,
            albumArtistRepository,
            albumRepository,
            trackRepository,
            genreMappingRepository,
            performanceRepository
        )
    }

}