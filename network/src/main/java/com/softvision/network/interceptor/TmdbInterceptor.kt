package com.softvision.network.interceptor

import com.softvision.network.BuildConfig
import com.softvision.network.model.LoginRequestData
import com.softvision.network.model.SessionIdRequestData
import com.softvision.network.model.SessionIdResponse
import com.softvision.network.service.AuthService
import com.softvision.preferences.TmdbSharedPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

internal class TmdbInterceptor @Inject constructor(
    private val tmdbSharedPreferences: TmdbSharedPreferences,
    private val authService: AuthService,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val apiKey = BuildConfig.TMDB_API_KEY_V3

        val originalUrl = chain.request().url
        val urlBuilder = originalUrl.newBuilder().addQueryParameter(API_KEY, apiKey)
        if (originalUrl.needsSessionId())
            urlBuilder.addSessionId()
        val request = chain.request().newBuilder()
            .url(urlBuilder.build())
            .build()
        return chain.proceed(request)
    }

    private fun HttpUrl.needsSessionId(): Boolean {
        return this.toUrl().path.contains(ACCOUNT_RELATED_REQUEST)
    }

    private fun HttpUrl.Builder.addSessionId() {
        val sessionId = tmdbSharedPreferences.getPreference<String>(TmdbSharedPreferences.TmdbPreferenceKey.SESSION_ID)
        if (sessionId == null) {
            val newSessionIdResponse = refreshToken()
            tmdbSharedPreferences.savePreference(TmdbSharedPreferences.TmdbPreferenceKey.SESSION_ID, newSessionIdResponse.sessionId)
            addQueryParameter(SESSION_ID_KEY, newSessionIdResponse.sessionId)
        } else
            addQueryParameter(SESSION_ID_KEY, sessionId)
    }

    private fun refreshToken(): SessionIdResponse {
        return runBlocking {
            val requestTokenResponse = authService.getRequestToken()
            val validateTokenResponse = authService.validateRequestToken(LoginRequestData(BuildConfig.ACCOUNT_ID, BuildConfig.ACCOUNT_PASSWORD, requestTokenResponse.requestToken))
            return@runBlocking authService.getSessionId(SessionIdRequestData(validateTokenResponse.requestToken))
        }
    }


    companion object {
        private const val SESSION_ID_KEY = "session_id"
        private const val API_KEY = "api_key"
        private const val ACCOUNT_RELATED_REQUEST = "account"
    }
}