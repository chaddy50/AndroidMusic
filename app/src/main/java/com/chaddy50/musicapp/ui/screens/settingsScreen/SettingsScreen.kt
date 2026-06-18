package com.chaddy50.musicapp.ui.screens.settingsScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chaddy50.musicapp.navigation.ClassicalGenreSettingsRoute
import com.chaddy50.musicapp.ui.screens.settingsScreen.genreMappings.ClassicalGenreSettingsRow
import com.chaddy50.musicapp.ui.screens.settingsScreen.listenBrainzLogin.ListenBrainzLogin

@Composable
fun SettingsScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ListenBrainzLogin()
        ClassicalGenreSettingsRow(onClick = { navController.navigate(ClassicalGenreSettingsRoute) })
    }
}
