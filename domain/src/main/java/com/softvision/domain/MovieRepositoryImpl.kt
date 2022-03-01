package com.softvision.domain

import com.softvision.database.LocalDataSource
import com.softvision.model.account.AccountDetails
import com.softvision.model.genre.Genre
import com.softvision.model.media.*
import com.softvision.network.RemoteDataSource
import javax.inject.Inject


internal class MovieRepositoryImpl @Inject internal constructor(
    private val remoteSource: RemoteDataSource,
    private val localSource: LocalDataSource,
) : MovieRepository {

    override suspend fun getCategory(page: Int, sortType: SortType): ItemResult<TmdbItem> =
        localSource.runCatching {
            val movies = getMovies(page, sortType)
            val tvShows = getTvShows(page, sortType)
            combineResults(movies, tvShows)
        }.getOrElse {
            val movies = remoteSource.getMovies(page, sortType)
            val tvShows = remoteSource.getTvShows(page, sortType)
            localSource.saveMovies(movies, sortType)
            localSource.saveTvShows(tvShows, sortType)
            combineResults(movies, tvShows)
        }

    override suspend fun getMovies(page: Int, sortType: SortType): ItemResult<Movie> =
        localSource.runCatching {
            getMovies(page, sortType)
        }.getOrElse {
            val movies = remoteSource.getMovies(page, sortType)
            localSource.saveMovies(movies, sortType)
            movies
        }

    override suspend fun getMovies(page: Int, genre: Genre): ItemResult<Movie> =
        localSource.runCatching {
            getMovies(page, genre.id)
        }.getOrElse {
            val movies = remoteSource.getMovies(page, genre.id)
            localSource.saveMovies(movies, genre.id)
            movies
        }

    override suspend fun getTvShows(page: Int, sortType: SortType): ItemResult<TvShow> =
        localSource.runCatching {
            getTvShows(page, sortType)
        }.getOrElse {
            val tvShows = remoteSource.getTvShows(page, sortType)
            localSource.saveTvShows(tvShows, sortType)
            tvShows
        }

    override suspend fun getTvShows(page: Int, genre: Genre): ItemResult<TvShow> =
        localSource.runCatching {
            getTvShows(page, genre.id)
        }.getOrElse {
            val tvShows = remoteSource.getTvShows(page, genre.id)
            localSource.saveTvShows(tvShows, genre.id)
            tvShows
        }

    override suspend fun search(page: Int, query: String): ItemResult<TmdbItem> =
        remoteSource.search(page, query)

    override suspend fun getMovieGenres(genreIds: List<Long>?): List<Genre> =
        localSource.runCatching {
            getMovieGenres(genreIds)
        }.getOrElse {
            val genres = remoteSource.getMovieGenres(genreIds)
            localSource.saveGenres(genres, MediaType.MOVIE)
            genres
        }

    override suspend fun getTvShowGenres(genreIds: List<Long>?): List<Genre> =
        localSource.runCatching {
            getTvShowGenres(genreIds)
        }.getOrElse {
            val genres = remoteSource.getTvShowGenres(genreIds)
            localSource.saveGenres(genres, MediaType.TV_SHOW)
            genres
        }

    override suspend fun getAccountDetails(): AccountDetails =
        localSource.runCatching {
            getAccountDetails()
        }.getOrElse {
            val accountDetails = remoteSource.getAccountDetails()
            localSource.saveAccountDetails(accountDetails)
            accountDetails
        }

    override suspend fun getFavoriteItems(page: Int): ItemResult<TmdbItem> {
        val accountDetails = remoteSource.getAccountDetails()
        return remoteSource.getFavoriteItems(accountDetails.id, page)
    }

    override suspend fun setFavorite(itemId: Long, mediaType: MediaType, favorite: Boolean): Boolean {
        val accountDetails = getAccountDetails()
        return remoteSource.setFavorite(accountDetails.id, itemId, mediaType, favorite)
    }

    override suspend fun isItemFavorite(itemId: Long, mediaType: MediaType): Boolean =
        remoteSource.isItemFavorite(itemId, mediaType)

    private fun combineResults(movies: ItemResult<Movie>, tvShows: ItemResult<TvShow>): ItemResult<TmdbItem> {
        return ItemResult(
            movies.page,
            movies.results.plus(tvShows.results),
            movies.totalResults + tvShows.totalResults,
            maxOf(movies.totalPages, tvShows.totalPages)
        )
    }
}
