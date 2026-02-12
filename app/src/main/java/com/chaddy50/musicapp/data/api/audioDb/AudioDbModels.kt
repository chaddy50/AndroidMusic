package com.chaddy50.musicapp.data.api.audioDb

import com.google.gson.annotations.SerializedName

data class AudioDbArtistSearchResponse(
    val artists: List<AudioDbArtist>?,
)

data class AudioDbArtist(
    @SerializedName("idArtist") val id: String,
    @SerializedName("strArtist") val name: String,
    @SerializedName("strArtistThumb") val thumbnailUrl: String?,
    @SerializedName("strGenre") val genre: String?,
    @SerializedName("strCountry") val country: String?,
    @SerializedName("intFormedYear") val formedYear: String?,
    @SerializedName("strBiographyEN") val biography: String?,
)
