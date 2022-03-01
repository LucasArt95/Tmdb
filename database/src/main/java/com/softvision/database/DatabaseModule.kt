package com.softvision.database

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    internal fun getDatabase(@ApplicationContext context: Context) = TmdbDatabase.getDatabase(context)

    @Singleton
    @Provides
    internal fun getMovieDao(TmdbDatabase: TmdbDatabase) = TmdbDatabase.movieDao()

    @Singleton
    @Provides
    internal fun getTvShowDao(TmdbDatabase: TmdbDatabase) = TmdbDatabase.tvShowsDao()

    @Singleton
    @Provides
    internal fun getResultsDao(TmdbDatabase: TmdbDatabase) = TmdbDatabase.resultsDao()

    @Singleton
    @Provides
    internal fun getGenresDao(TmdbDatabase: TmdbDatabase) = TmdbDatabase.genresDao()
}