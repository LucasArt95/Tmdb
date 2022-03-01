package com.softvision.network.interceptor

import com.softvision.network.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

internal class ApiKeyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val apiKey = BuildConfig.TMDB_API_KEY_V3;
        val originalUrl = chain.request().url
        val urlBuilder = originalUrl.newBuilder().addQueryParameter(API_KEY, apiKey)
        val request = chain.request().newBuilder().url(urlBuilder.build()).build()
        return chain.proceed(request)
    }

    companion object {
        private const val API_KEY = "api_key"
    }
}