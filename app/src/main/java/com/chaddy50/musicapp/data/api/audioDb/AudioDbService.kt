package com.chaddy50.musicapp.data.api.audioDb

import retrofit2.http.GET
import retrofit2.http.Query

interface AudioDbService {
    @GET("search.php")
    suspend fun searchArtist(@Query("s") name: String): AudioDbArtistSearchResponse
}
