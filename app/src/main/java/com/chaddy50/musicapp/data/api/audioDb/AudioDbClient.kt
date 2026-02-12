package com.chaddy50.musicapp.data.api.audioDb

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AudioDbClient {
    private const val BASE_URL = "https://www.theaudiodb.com/api/v1/json/2/"

    val service: AudioDbService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AudioDbService::class.java)
    }
}
