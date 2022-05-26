package com.softvision.network.interceptor

import com.github.vacxe.konveyor.base.randomBuild
import com.softvision.network.BuildConfig
import com.softvision.network.model.SessionIdResponse
import com.softvision.network.service.AuthService
import com.softvision.preferences.TmdbSharedPreferences
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.util.*

class InterceptorsTest {

    private val apiKeyInterceptor = ApiKeyInterceptor()

    @MockK
    private lateinit var tmdbSharedPreferences: TmdbSharedPreferences

    @MockK
    private lateinit var authService: AuthService
    private lateinit var mockWebServer: MockWebServer

    private lateinit var tmdbInterceptor: TmdbInterceptor

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        mockWebServer = MockWebServer()
        mockWebServer.start()
        tmdbInterceptor = TmdbInterceptor(tmdbSharedPreferences, authService)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `ApiKeyInterceptor appends API key in query path`() {
        val apiKey: String = BuildConfig.TMDB_API_KEY_V3
        val queryString = "api_key=$apiKey"
        mockWebServer.enqueue(MockResponse())
        val okHttpClient: OkHttpClient = OkHttpClient().newBuilder().addInterceptor(apiKeyInterceptor).build()
        okHttpClient.newCall(Request.Builder().url(mockWebServer.url("/")).build()).execute()

        val request = mockWebServer.takeRequest()
        assert(request.requestUrl?.toString()?.contains(queryString) ?: false)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `TmdbInterceptor appends API key and sessionId in query path`() = runTest {
        val apiKey: String = BuildConfig.TMDB_API_KEY_V3
        val apiKeyQueryString = "api_key=$apiKey"
        val sessionId = UUID.randomUUID().toString()
        val sessionIdQueryString = "session_id=$sessionId"
        mockWebServer.enqueue(MockResponse())
        val okHttpClient: OkHttpClient = OkHttpClient().newBuilder().addInterceptor(tmdbInterceptor).build()
        coEvery { authService.getRequestToken() } returns randomBuild()
        coEvery { authService.validateRequestToken(any()) } returns randomBuild()
        coEvery { authService.getSessionId(any()) } returns SessionIdResponse(true, sessionId)
        coEvery { tmdbSharedPreferences.savePreference(any(), any<String>()) } returns Unit
        okHttpClient.newCall(Request.Builder().url(mockWebServer.url("/account/")).build()).execute()

        val request = mockWebServer.takeRequest()

        assert(request.requestUrl?.toString()?.contains(apiKeyQueryString) ?: false)
        assert(request.requestUrl?.toString()?.contains(sessionIdQueryString) ?: false)
    }
}