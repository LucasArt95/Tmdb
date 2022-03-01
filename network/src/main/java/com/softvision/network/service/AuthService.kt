package com.softvision.network.service

import com.softvision.network.model.LoginRequestData
import com.softvision.network.model.RequestTokenResponse
import com.softvision.network.model.SessionIdRequestData
import com.softvision.network.model.SessionIdResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

internal interface AuthService {

    @GET("authentication/token/new")
    suspend fun getRequestToken(): RequestTokenResponse

    @POST("authentication/token/validate_with_login")
    suspend fun validateRequestToken(@Body loginRequestData: LoginRequestData): RequestTokenResponse

    @POST("authentication/session/new")
    suspend fun getSessionId(@Body sessionIdRequestData: SessionIdRequestData): SessionIdResponse

}