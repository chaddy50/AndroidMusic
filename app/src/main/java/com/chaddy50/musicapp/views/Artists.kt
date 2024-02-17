package com.chaddy50.musicapp.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.chaddy50.musicapp.components.TopBar

@Composable
fun Artists() {
    Scaffold(
        topBar = { TopBar("Artists") }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
        }
    }
}