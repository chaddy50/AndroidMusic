package com.chaddy50.froh.ui.screens.settingsScreen.listenBrainzLogin.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.chaddy50.froh.data.api.listenBrainz.TokenValidationState

@Composable
fun LoggedOutContent(
    validationState: TokenValidationState,
    onSaveToken: (String) -> Unit,
) {
    var tokenInput by rememberSaveable { mutableStateOf("") }

    Text(
        text = "Log your listens to ListenBrainz. Enter your API token from listenbrainz.org/settings/ to get started.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    OutlinedTextField(
        value = tokenInput,
        onValueChange = { tokenInput = it },
        label = { Text("API Token") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    )

    Button(
        onClick = { onSaveToken(tokenInput) },
        enabled = tokenInput.isNotBlank() && validationState !is TokenValidationState.Validating,
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (validationState is TokenValidationState.Validating) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        } else {
            Text("Validate & Save")
        }
    }

    when (validationState) {
        is TokenValidationState.Invalid -> {
            Text(
                text = "Token is invalid. Check your token and try again.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
        is TokenValidationState.NetworkError -> {
            Text(
                text = "Network error. Check your connection and try again.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
        else -> {}
    }
}