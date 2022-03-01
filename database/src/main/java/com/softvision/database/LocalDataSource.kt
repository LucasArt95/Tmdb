package com.softvision.database

import com.softvision.database.dao.GenresDao
import com.softvision.database.dao.MovieDao
import com.softvision.database.dao.ResultsDao
import com.softvision.database.dao.TvShowDao
import com.softvision.database.model.*
import com.softvision.model.account.AccountDetails
import com.softvision.model.genre.Genre
import com.softvision.model.media.*
import com.softvision.preferences.TmdbSharedPreferences
import com.softvision.preferences.TmdbSharedPreferences.TmdbPreferenceKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSource @Inject internal constructor(
    private val movieDao: MovieDao,
    private val tvShowDao: TvShowDao,
    private val genresDao: GenresDao,
    private val resultsDao: ResultsDao,
    private val tmdbSharedPreferences: TmdbSharedPreferences
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    suspend fun getMovies(page: Int, sortType: SortType): ItemResult<Movie> {
        val movieResultsWithMovies = resultsDao.getMovieResultWithMovies(page, sortType)
        return ItemResult(
            page = movieResultsWithMovies.result.page,
            results = movieResultsWithMovies.movies.toMovies(),
            totalResults = movieResultsWithMovies.result.totalResults,
            totalPages = movieResultsWithMovies.result.totalPages
        )
    }

    suspend fun getMovies(page: Int, genre: Long): ItemResult<Movie> {
        val movieResultsWithMovies = resultsDao.getMovieResultWithMovies(page, genre)
        return ItemResult(
            page = movieResultsWithMovies.result.page,
            results = movieResultsWithMovies.movies.toMovies(),
            totalResults = movieResultsWithMovies.result.totalResults,
            totalPages = movieResultsWithMovies.result.totalPages
        )
    }

    suspend fun getTvShows(page: Int, sortType: SortType): ItemResult<TvShow> {
        val tvShowResultsWithTvShows = resultsDao.getTvShowResultWithTvShows(page, sortType)
        return ItemResult(
            page = tvShowResultsWithTvShows.result.page,
            results = tvShowResultsWithTvShows.tvShows.toTvShows(),
            totalResults = tvShowResultsWithTvShows.result.totalResults,
            totalPages = tvShowResultsWithTvShows.result.totalPages
        )
    }

    suspend fun getTvShows(page: Int, genre: Long): ItemResult<TvShow> {
        val tvShowResultsWithTvShows = resultsDao.getTvShowResultWithTvShows(page, genre)
        return ItemResult(
            page = tvShowResultsWithTvShows.result.page,
            results = tvShowResultsWithTvShows.tvShows.toTvShows(),
            totalResults = tvShowResultsWithTvShows.result.totalResults,
            totalPages = tvShowResultsWithTvShows.result.totalPages
        )
    }

    suspend fun getMovieGenres(genreIds: List<Long>?): List<Genre> {
        val genres = genresDao.getGenres(MediaType.MOVIE)
        if (genres.isNullOrEmpty()) throw EmptyTableException("The Genres Table is empty")
        return genres.filter { genre -> genreIds?.contains(genre.id) ?: true }.map { Genre(it.id, it.name) }
    }

    suspend fun getTvShowGenres(genreIds: List<Long>?): List<Genre> {
        val genres = genresDao.getGenres(MediaType.TV_SHOW)
        if (genres.isNullOrEmpty()) throw EmptyTableException("The Genres Table is empty")
        return genres.filter { genre -> genreIds?.contains(genre.id) ?: true }.map { Genre(it.id, it.name) }
    }

    fun getAccountDetails(): AccountDetails {
        val accountDetails = tmdbSharedPreferences.getPreference<AccountDetails>(TmdbPreferenceKey.ACCOUNT_DETAILS)
        return accountDetails ?: throw EmptyPreferenceException("Account details not found")
    }

    fun saveMovies(itemResult: ItemResult<Movie>, sortType: SortType) {
        scope.launch {
            movieDao.saveMovies(itemResult.results.toMovieEntity())
            val resultId = resultsDao.saveMovieResult(
                MovieResultEntity(
                    page = itemResult.page,
                    totalResults = itemResult.totalResults,
                    totalPages = itemResult.totalPages,
                    sortType = sortType
                )
            )
            itemResult.results.forEach {
                resultsDao.saveMovieResultCrossRef(MovieResultCrossRef(it.itemId, resultId))
            }
        }
    }

    fun saveMovies(itemResult: ItemResult<Movie>, genreId: Long) {
        scope.launch {
            movieDao.saveMovies(itemResult.results.toMovieEntity())
            val resultId = resultsDao.saveMovieResult(
                MovieResultEntity(
                    page = itemResult.page,
                    totalResults = itemResult.totalResults,
                    totalPages = itemResult.totalPages,
                    genreId = genreId
                )
            )
            itemResult.results.forEach {
                resultsDao.saveMovieResultCrossRef(MovieResultCrossRef(it.itemId, resultId))
            }
        }
    }

    fun saveTvShows(itemsResult: ItemResult<TvShow>, sortType: SortType) {
        scope.launch {
            tvShowDao.saveTvShows(itemsResult.results.toTvShowEntity())
            val resultId = resultsDao.saveTvShowResult(
                TvShowResultEntity(
                    page = itemsResult.page,
                    totalResults = itemsResult.totalResults,
                    totalPages = itemsResult.totalPages,
                    sortType = sortType
                )
            )
            itemsResult.results.forEach { tvShow ->
                resultsDao.saveTvShowResultCrossRef(TvShowResultCrossRef(tvShow.itemId, resultId))
            }
        }
    }

    fun saveTvShows(itemsResult: ItemResult<TvShow>, genreId: Long) {
        scope.launch {
            tvShowDao.saveTvShows(itemsResult.results.toTvShowEntity())
            val resultId = resultsDao.saveTvShowResult(
                TvShowResultEntity(
                    page = itemsResult.page,
                    totalResults = itemsResult.totalResults,
                    totalPages = itemsResult.totalPages,
                    genreId = genreId
                )
            )
            itemsResult.results.forEach { tvShow ->
                resultsDao.saveTvShowResultCrossRef(TvShowResultCrossRef(tvShow.itemId, resultId))
            }
        }
    }

    fun saveGenres(genres: List<Genre>, mediaType: MediaType) {
        scope.launch {
            genresDao.saveGenres(genres.map { GenreEntity(it.id, it.name, mediaType) })
        }
    }

    fun saveAccountDetails(accountDetails: AccountDetails) {
        tmdbSharedPreferences.savePreference(TmdbPreferenceKey.ACCOUNT_DETAILS, accountDetails)
    }


    internal inner class EmptyPreferenceException(message: String) : Exception(message)

    internal inner class EmptyTableException(message: String) : Exception(message)

}

private fun List<MovieEntity>.toMovies(): List<Movie> =
    this.map {
        Movie(
            itemId = it.itemId,
            name = it.name,
            originalName = it.originalName,
            releaseDate = it.releaseDate,
            originalLanguage = it.originalLanguage,
            genreIds = it.genreIds,
            popularity = it.popularity,
            voteCount = it.voteCount,
            voteAverage = it.voteAverage,
            overview = it.overview,
            posterPath = it.posterPath,
            backdropPath = it.backdropPath,
            favorite = it.favorite,
            adult = it.adult,
            video = it.video
        )
    }

private fun List<Movie>.toMovieEntity(): List<MovieEntity> =
    this.map {
        MovieEntity(
            itemId = it.itemId,
            name = it.name,
            originalName = it.originalName,
            releaseDate = it.releaseDate,
            originalLanguage = it.originalLanguage,
            genreIds = it.genreIds,
            popularity = it.popularity,
            voteCount = it.voteCount,
            voteAverage = it.voteAverage,
            overview = it.overview,
            posterPath = it.posterPath,
            backdropPath = it.backdropPath,
            favorite = it.favorite,
            adult = it.adult,
            video = it.video
        )
    }

private fun List<TvShowEntity>.toTvShows(): List<TvShow> =
    this.map {
        TvShow(
            itemId = it.itemId,
            name = it.name,
            originalName = it.originalName,
            releaseDate = it.releaseDate,
            originalLanguage = it.originalLanguage,
            genreIds = it.genreIds,
            popularity = it.popularity,
            voteCount = it.voteCount,
            voteAverage = it.voteAverage,
            overview = it.overview,
            posterPath = it.posterPath,
            backdropPath = it.backdropPath,
            favorite = it.favorite,
            originCountry = it.originCountry
        )
    }

private fun List<TvShow>.toTvShowEntity(): List<TvShowEntity> =
    this.map {
        TvShowEntity(
            itemId = it.itemId,
            name = it.name,
            originalName = it.originalName,
            releaseDate = it.releaseDate,
            originalLanguage = it.originalLanguage,
            genreIds = it.genreIds,
            popularity = it.popularity,
            voteCount = it.voteCount,
            voteAverage = it.voteAverage,
            overview = it.overview,
            posterPath = it.posterPath,
            backdropPath = it.backdropPath,
            favorite = it.favorite,
            originCountry = it.originCountry
        )
    }
