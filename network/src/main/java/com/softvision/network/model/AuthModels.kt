package com.softvision.network.model

import com.google.gson.annotations.SerializedName


internal data class RequestTokenResponse(
    val success: Boolean,
    @SerializedName("expires_at")
    val expiresAt: String,
    @SerializedName("request_token")
    val requestToken: String,
)

internal data class SessionIdResponse(
    val success: Boolean,
    @SerializedName("session_id")
    val sessionId: String,
)

internal data class LoginRequestData(
    val username: String,
    val password: String,
    @SerializedName("request_token")
    val requestToken: String,
)

internal data class SessionIdRequestData(
    @SerializedName("request_token")
    val requestToken: String,
)