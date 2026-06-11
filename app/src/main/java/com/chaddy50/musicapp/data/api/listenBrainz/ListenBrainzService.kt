package com.chaddy50.musicapp.data.api.listenBrainz

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ListenBrainzService {
    @POST("submit-listens")
    suspend fun submitListens(
        @Header("Authorization") token: String,
        @Body request: ListenBrainzSubmitRequest
    ): ListenBrainzSubmitResponse

    @GET("validate-token")
    suspend fun validateToken(
        @Header("Authorization") token: String
    ): ValidateTokenResponse

    @POST("playing-now/delete")
    suspend fun deletePlayingNow(
        @Header("Authorization") token: String
    )
}
