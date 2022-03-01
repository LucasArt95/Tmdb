package com.softvision.domain

import com.softvision.model.account.AccountDetails
import com.softvision.model.genre.Genre
import com.softvision.model.media.*


interface MovieRepository {

    suspend fun getCategory(page: Int, sortType: SortType): ItemResult<TmdbItem>

    suspend fun getMovies(page: Int, sortType: SortType): ItemResult<Movie>

    suspend fun getTvShows(page: Int, sortType: SortType): ItemResult<TvShow>

    suspend fun search(page: Int, query: String): ItemResult<TmdbItem>

    suspend fun getMovieGenres(genreIds: List<Long>? = null): List<Genre>

    suspend fun getTvShowGenres(genreIds: List<Long>? = null): List<Genre>

    suspend fun getMovies(page: Int, genre: Genre): ItemResult<Movie>

    suspend fun getTvShows(page: Int, genre: Genre): ItemResult<TvShow>

    suspend fun getAccountDetails(): AccountDetails

    suspend fun getFavoriteItems(page: Int): ItemResult<TmdbItem>

    suspend fun setFavorite(itemId: Long, mediaType: MediaType, favorite: Boolean): Boolean

    suspend fun isItemFavorite(itemId: Long,mediaType: MediaType): Boolean

    companion object {
        const val INITIAL_PAGE = 1
    }
}