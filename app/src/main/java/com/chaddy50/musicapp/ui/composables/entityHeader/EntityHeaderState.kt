package com.chaddy50.musicapp.ui.composables.entityHeader

data class EntityHeaderState(
    val title: String = "Title",
    val subtitle: String = "Subtitle",
    val details: String? = null,
    val artworkPath: String? = null,
    val isLoading: Boolean = true,
)
