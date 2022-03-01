package com.softvision.network

import com.softvision.model.account.AccountDetails
import com.softvision.model.genre.Genre
import com.softvision.model.media.*
import com.softvision.network.model.FavoriteRequestData
import com.softvision.network.service.TmdbService
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject internal constructor(private val tmdbService: TmdbService) {

    private val releaseDate = buildReleaseDate()

    suspend fun getMovies(page: Int, sortType: SortType): ItemResult<Movie> =
        when (sortType) {
            SortType.POPULAR -> tmdbService.getPopularMovies(page)
            SortType.TOP_RATED -> tmdbService.getTopRatedMovies(page)
            SortType.TRENDING -> tmdbService.getTrendingMovies(page)
            SortType.UPCOMING -> tmdbService.getUpcomingMovies(page, releaseDate)
            SortType.NOW_PLAYING -> tmdbService.getNowPlayingMovies(page)
        }

    suspend fun getMovieGenres(genreIds: List<Long>?): List<Genre> {
        val genreResult = tmdbService.getMovieGenres()
        return genreResult.genres.filter { genre -> genreIds?.contains(genre.id) ?: true }
    }

    suspend fun getMovies(page: Int, genre: Long): ItemResult<Movie> =
        tmdbService.getMoviesByGenres(page, listOf(genre))


    suspend fun getTvShows(page: Int, sortType: SortType): ItemResult<TvShow> =
        when (sortType) {
            SortType.POPULAR -> tmdbService.getPopularTvShows(page)
            SortType.TOP_RATED -> tmdbService.getTopRatedTvShows(page)
            SortType.TRENDING -> tmdbService.getTrendingTvShows(page)
            SortType.UPCOMING -> tmdbService.getUpcomingTvShows(page, releaseDate)
            SortType.NOW_PLAYING -> tmdbService.getOnTheAirTvShows(page)
        }

    suspend fun getTvShowGenres(genreIds: List<Long>?): List<Genre> {
        val genreResult = tmdbService.getTvShowGenres()
        return genreResult.genres.filter { genre -> genreIds?.contains(genre.id) ?: true }
    }

    suspend fun getTvShows(page: Int, genre: Long): ItemResult<TvShow> =
        tmdbService.getTvShowsByGenres(page, listOf(genre))

    suspend fun search(page: Int, query: String): ItemResult<TmdbItem> {
        val result = tmdbService.search(query, page)
        return result.copy(results = result.results.filterNotNull())
    }

    suspend fun getAccountDetails(): AccountDetails =
        tmdbService.getAccountDetails()

    suspend fun getFavoriteItems(accountId: Int, page: Int): ItemResult<TmdbItem> {
        val movies = tmdbService.getFavoriteMovies(accountId, page)
        val tvShows = tmdbService.getFavoriteTvShows(accountId, page)
        return combineResults(movies, tvShows)
    }

    suspend fun setFavorite(accountId: Int, id: Long, mediaType: MediaType, favorite: Boolean): Boolean {
        tmdbService.setFavorite(accountId, FavoriteRequestData(mediaType.type, id, favorite))
        return favorite
    }

    suspend fun isItemFavorite(itemId: Long, mediaType: MediaType) =
        when (mediaType) {
            MediaType.MOVIE -> tmdbService.isMovieFavorite(itemId).favorite
            MediaType.TV_SHOW -> tmdbService.isTvShowFavorite(itemId).favorite
        }

    private fun combineResults(
        movies: ItemResult<Movie>,
        tvShows: ItemResult<TvShow>,
    ): ItemResult<TmdbItem> {
        return ItemResult(
            movies.page,
            movies.results.plus(tvShows.results),
            movies.totalResults + tvShows.totalResults,
            maxOf(movies.totalPages, tvShows.totalPages)
        )
    }

    private fun buildReleaseDate(): String {
        val calendar = Calendar.getInstance()
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        return "$year-$month-$dayOfMonth"
    }

}