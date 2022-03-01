package com.softvision.network.interceptor

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class TmdbInterceptorOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
internal annotation class ApiKeyInterceptorOkHttpClient