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
import com.chaddy50.musicapp.data.repository.AlbumArtistRepository
import com.chaddy50.musicapp.data.repository.AlbumRepository
import com.chaddy50.musicapp.data.repository.ArtistRepository
import com.chaddy50.musicapp.data.repository.ComposerRepository
import com.chaddy50.musicapp.data.repository.GenreRepository
import com.chaddy50.musicapp.data.repository.PerformanceRepository
import com.chaddy50.musicapp.data.repository.PlaylistRepository
import com.chaddy50.musicapp.data.repository.TrackRepository
import com.chaddy50.musicapp.data.scanner.LibraryScanViewModel
import com.chaddy50.musicapp.ui.composables.nowPlayingBar.PlaybackViewModel
import com.chaddy50.musicapp.ui.screens.playlistsScreen.PlaylistViewModel
import com.chaddy50.musicapp.ui.theme.MusicAppTheme
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MusicApp : ComponentActivity() {
    private val playbackViewModel: PlaybackViewModel by viewModels()
    private val playlistViewModel: PlaylistViewModel by viewModels()
    private val libraryScanViewModel: LibraryScanViewModel by viewModels()

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
                    NavigationHost(playbackViewModel, playlistViewModel, libraryScanViewModel)
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
            triggerLibraryScanIfNeeded()
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
            libraryScanViewModel.refreshLibrary()
        }
    }
}

@HiltAndroidApp
class MusicApplication: Application() {
    var classicalGenreId: Long? = null

    @Inject lateinit var trackRepository: TrackRepository
    @Inject lateinit var albumRepository: AlbumRepository
    @Inject lateinit var artistRepository: ArtistRepository
    @Inject lateinit var genreRepository: GenreRepository
    @Inject lateinit var albumArtistRepository: AlbumArtistRepository
    @Inject lateinit var performanceRepository: PerformanceRepository
    @Inject lateinit var playlistRepository: PlaylistRepository
    @Inject lateinit var composerRepository: ComposerRepository
}
