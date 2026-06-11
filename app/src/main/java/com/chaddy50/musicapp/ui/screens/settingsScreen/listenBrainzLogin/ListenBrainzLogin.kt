package com.chaddy50.musicapp.ui.screens.settingsScreen.listenBrainzLogin

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chaddy50.musicapp.data.api.listenBrainz.TokenValidationState
import com.chaddy50.musicapp.ui.screens.settingsScreen.listenBrainzLogin.composables.LoggedInContent
import com.chaddy50.musicapp.ui.screens.settingsScreen.listenBrainzLogin.composables.LoggedOutContent
@Composable
fun ListenBrainzLogin(
    viewModel: ListenBrainzLoginViewModel = hiltViewModel(),
) {
    val validationState by viewModel.tokenValidationState.collectAsStateWithLifecycle()

    val isLoggedIn = validationState is TokenValidationState.Valid

    Text(
        text = "ListenBrainz",
        style = MaterialTheme.typography.titleLarge,
    )

    if (isLoggedIn) {
        LoggedInContent(
            userName = (validationState as TokenValidationState.Valid).userName,
            onLogout = { viewModel.logout() },
        )
    } else {
        LoggedOutContent(
            validationState = validationState,
            onSaveToken = { viewModel.saveToken(it) },
        )
    }
}
