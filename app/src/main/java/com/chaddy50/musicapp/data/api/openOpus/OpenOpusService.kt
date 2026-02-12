package com.chaddy50.musicapp.data.api.openOpus

import retrofit2.http.GET
import retrofit2.http.Path

interface OpenOpusService {
    @GET("composer/list/search/{query}.json")
    suspend fun searchComposers(@Path("query") query: String): ComposerSearchResponse
}
