package com.chaddy50.musicapp.ui.composables

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavController

@SuppressLint("RestrictedApi")
@Composable
fun CleanUpWhenNavigatingBackEffect(
    navController: NavController,
    route: String,
    cleanUp: () -> Unit
) {
    DisposableEffect(navController) {
        onDispose {
            val backStack = navController.currentBackStack.value
            val isStillOnStack = backStack.any { it.destination.route == route }

            if (!isStillOnStack) {
                cleanUp()
            }
        }
    }
}