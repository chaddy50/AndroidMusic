package com.chaddy50.musicapp.data.api.listenBrainz

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ListenBrainzClient {
    private const val BASE_URL = "https://api.listenbrainz.org/1/"

    val service: ListenBrainzService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ListenBrainzService::class.java)
    }
}
