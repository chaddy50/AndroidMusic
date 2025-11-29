package com.chaddy50.musicapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController

interface MusicAppScreen {
    val route: String

    val arguments: List<NamedNavArgument>
        get() = emptyList()

    val routeWithArgs: String
        get() {
            val optionalArgs = arguments.filter { it.argument.defaultValue != null }
            val requiredArgs = arguments.filter { it.argument.defaultValue == null }

            val optionalPart = optionalArgs.joinToString(separator = "&", prefix = "?") { it.name + "={" + it.name + "}" }
            val requiredPart = requiredArgs.joinToString(separator = "/", prefix = "/") { "{" + it.name + "}" }

            return when {
                optionalArgs.isNotEmpty() && requiredArgs.isEmpty() -> route + optionalPart
                requiredArgs.isNotEmpty() && optionalArgs.isEmpty() -> route + requiredPart
                // Handle mixed case if needed, though optional is more common for this structure
                else -> route
            }
        }

    @Composable
    fun Content(
        navController: NavController,
        backStackEntry: NavBackStackEntry
    )
}