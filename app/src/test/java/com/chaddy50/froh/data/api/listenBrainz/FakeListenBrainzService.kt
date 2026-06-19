package com.chaddy50.froh.data.api.listenBrainz

class FakeListenBrainzService(
    private val onSubmitListens: (String, ListenBrainzSubmitRequest) -> ListenBrainzSubmitResponse = { _, _ ->
        ListenBrainzSubmitResponse(status = "ok")
    },
    private val onValidateToken: (String) -> ValidateTokenResponse = { _ ->
        ValidateTokenResponse(code = 200, message = "Token valid.", valid = true, user_name = "testuser")
    },
) : ListenBrainzService {

    val submitCalls = mutableListOf<Pair<String, ListenBrainzSubmitRequest>>()
    val validateCalls = mutableListOf<String>()

    override suspend fun submitListens(
        token: String,
        request: ListenBrainzSubmitRequest
    ): ListenBrainzSubmitResponse {
        submitCalls.add(token to request)
        return onSubmitListens(token, request)
    }

    override suspend fun validateToken(token: String): ValidateTokenResponse {
        validateCalls.add(token)
        return onValidateToken(token)
    }

    override suspend fun deletePlayingNow(token: String) {
    }
}
