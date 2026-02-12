package com.chaddy50.musicapp.data.api.openOpus

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object OpenOpusClient {
    private const val BASE_URL = "https://api.openopus.org/"

    val service: OpenOpusService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenOpusService::class.java)
    }
}
