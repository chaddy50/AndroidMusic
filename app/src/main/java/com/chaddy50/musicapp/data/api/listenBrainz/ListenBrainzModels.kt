package com.chaddy50.musicapp.data.api.listenBrainz

data class ListenBrainzSubmitRequest(
    val listen_type: String,
    val payload: List<ListenPayload>
)

data class ListenPayload(
    val listened_at: Long? = null,
    val track_metadata: TrackMetadata
)

data class TrackMetadata(
    val artist_name: String,
    val track_name: String,
    val release_name: String? = null,
    val additional_info: AdditionalInfo? = null
)

data class AdditionalInfo(
    val tracknumber: String? = null,
    val duration_ms: Long? = null,
    val submission_client: String? = null,
    val submission_client_version: String? = null,
)

data class ListenBrainzSubmitResponse(
    val status: String
)

data class ValidateTokenResponse(
    val code: Int,
    val message: String,
    val valid: Boolean,
    val user_name: String? = null
)
