package com.chaddy50.musicapp.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityHeader
import com.chaddy50.musicapp.ui.composables.entityHeader.EntityType
import com.chaddy50.musicapp.viewModel.MusicAppViewModel

@Composable
fun EntityScreen(
    viewModel: MusicAppViewModel,
    navController: NavController,
    entityType: EntityType,
    screenTitle: String,
    isLoading: Boolean,
    content: @Composable () -> Unit,
) {
    Scaffold(
        topBar = {
            TopBar(
                true,
                screenTitle,
                navController
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                EntityHeader(viewModel, entityType)

                content()
            }
        }
    }
}