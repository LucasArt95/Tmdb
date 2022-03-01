package com.softvision.network.service

import com.softvision.model.account.AccountDetails
import com.softvision.model.genre.GenreResult
import com.softvision.model.media.ItemResult
import com.softvision.model.media.Movie
import com.softvision.model.media.TmdbItem
import com.softvision.model.media.TvShow
import com.softvision.network.model.FavoriteItem
import com.softvision.network.model.FavoriteRequestData
import retrofit2.http.*

internal interface TmdbService {

    //  Movies
    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("page") page: Int): ItemResult<Movie>

    @GET("trending/movie/week")
    suspend fun getTrendingMovies(@Query("page") page: Int): ItemResult<Movie>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(@Query("page") page: Int): ItemResult<Movie>

    @GET("discover/movie")
    suspend fun getUpcomingMovies(@Query("page") page: Int, @Query("release_date.gte") releaseDateGreaterThen: String): ItemResult<Movie>

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(@Query("page") page: Int): ItemResult<Movie>

    @GET("discover/movie")
    suspend fun getMoviesByGenres(@Query("page") page: Int, @Query("with_genres") genres: List<Long>): ItemResult<Movie>

    @GET("genre/movie/list")
    suspend fun getMovieGenres(): GenreResult
    
    
    //  TV Shows
    @GET("tv/popular")
    suspend fun getPopularTvShows(@Query("page") page: Int): ItemResult<TvShow>

    @GET("trending/tv/week")
    suspend fun getTrendingTvShows(@Query("page") page: Int): ItemResult<TvShow>

    @GET("tv/top_rated")
    suspend fun getTopRatedTvShows(@Query("page") page: Int): ItemResult<TvShow>

    @GET("discover/tv")
    suspend fun getUpcomingTvShows(@Query("page") page: Int, @Query("release_date.gte") releaseDateGreaterThen: String): ItemResult<TvShow>

    @GET("tv/on_the_air")
    suspend fun getOnTheAirTvShows(@Query("page") page: Int): ItemResult<TvShow>

    @GET("discover/tv")
    suspend fun getTvShowsByGenres(@Query("page") page: Int, @Query("with_genres") genres: List<Long>): ItemResult<TvShow>

    @GET("genre/tv/list")
    suspend fun getTvShowGenres(): GenreResult

    
    //  Search
    @GET("search/multi")
    suspend fun search(@Query("query") query: String, @Query("page") page: Int): ItemResult<TmdbItem>

    
    //  Account
    @GET("account")
    suspend fun getAccountDetails(): AccountDetails

    @GET("account/{account_id}/favorite/movies")
    suspend fun getFavoriteMovies(@Path("account_id") accountId: Int, @Query("page") page: Int): ItemResult<Movie>

    @GET("account/{account_id}/favorite/tv")
    suspend fun getFavoriteTvShows(@Path("account_id") accountId: Int, @Query("page") page: Int): ItemResult<TvShow>

    @POST("account/{account_id}/favorite")
    suspend fun setFavorite(@Path("account_id") accountId: Int, @Body favoriteRequestData: FavoriteRequestData)

    @GET("movie/{movie_id}/account_states")
    suspend fun isMovieFavorite(@Path("movie_id") movieId: Long): FavoriteItem

    @GET("tv/{tv_id}/account_states")
    suspend fun isTvShowFavorite(@Path("tv_id") tvShowId: Long): FavoriteItem

}