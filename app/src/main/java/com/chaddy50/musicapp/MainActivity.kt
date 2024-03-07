package com.chaddy50.musicapp

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.chaddy50.musicapp.data.MusicDatabase
import com.chaddy50.musicapp.navigation.NavigationHost
import com.chaddy50.musicapp.ui.theme.MusicAppTheme
import com.chaddy50.musicapp.views.Home

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val musicDatabase = MusicDatabase(
            setOf(), setOf(), setOf(), setOf())
        setContent {
            MusicAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (ContextCompat.checkSelfPermission(
                            this.applicationContext,
                            Manifest.permission.READ_MEDIA_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        musicDatabase.initialize(this.applicationContext)
                    } else {
                        permissionRequestLauncher.launch(Manifest.permission.READ_MEDIA_AUDIO)
                    }
                    NavigationHost(LocalContext.current, musicDatabase)
                }
            }
        }
    }

    private val permissionRequestLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { hasPermission ->
            if (hasPermission) {
                this.recreate()
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


}