package com.softvision.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.softvision.model.media.TmdbItem
import com.softvision.network.interceptor.ApiKeyInterceptor
import com.softvision.network.interceptor.ApiKeyInterceptorOkHttpClient
import com.softvision.network.interceptor.TmdbInterceptor
import com.softvision.network.interceptor.TmdbInterceptorOkHttpClient
import com.softvision.network.service.AuthService
import com.softvision.network.service.TmdbService
import com.softvision.preferences.TmdbSharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object NetworkModule {

    private const val BASE_URL = "https://api.themoviedb.org/3/"

    @TmdbInterceptorOkHttpClient
    @Provides
    internal fun provideAuthInterceptorOkHttpClient(
        tmdbSharedPreferences: TmdbSharedPreferences,
        authService: AuthService
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)

        return OkHttpClient.Builder()
            .addInterceptor(TmdbInterceptor(tmdbSharedPreferences, authService))
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @ApiKeyInterceptorOkHttpClient
    @Provides
    internal fun provideOtherInterceptorOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)

        return OkHttpClient.Builder()
            .addInterceptor(ApiKeyInterceptor())
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    internal fun provideTmdbService(@TmdbInterceptorOkHttpClient okHttpClient: OkHttpClient, gson: Gson): TmdbService {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(TmdbService::class.java)
    }

    @Singleton
    @Provides
    internal fun provideAuthService(@ApiKeyInterceptorOkHttpClient okHttpClient: OkHttpClient, gson: Gson): AuthService {
        return Retrofit.Builder().baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(AuthService::class.java)
    }

    @Singleton
    @Provides
    internal fun provideGson(): Gson {
        return GsonBuilder().registerTypeAdapter(TmdbItem::class.java, TmdbItemDeserializer()).create()
    }
}