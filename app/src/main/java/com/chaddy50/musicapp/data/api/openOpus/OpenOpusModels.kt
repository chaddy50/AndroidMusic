package com.chaddy50.musicapp.data.api.openOpus

import com.google.gson.annotations.SerializedName

data class ComposerSearchResponse(
    val status: OpenOpusStatus?,
    val composers: List<OpenOpusComposer>?,
)

data class OpenOpusStatus(
    @SerializedName("success") private val _success: String?,
    val rows: Int?,
)

data class OpenOpusComposer(
    val id: Long,
    val name: String,
    @SerializedName("complete_name") val completeName: String,
    @SerializedName("birth") val birthDate: String?,
    @SerializedName("death") val deathDate: String?,
    val epoch: String?,
    @SerializedName("portrait") val portraitUrl: String?,
)
