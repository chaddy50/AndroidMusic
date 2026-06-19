package com.chaddy50.froh.ui.screens.settingsScreen.listenBrainzLogin.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoggedInContent(
    userName: String,
    onLogout: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "Logged in as $userName",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = "Listens will be scrobbled to ListenBrainz.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }

    OutlinedButton(
        onClick = onLogout,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Log Out")
    }
}