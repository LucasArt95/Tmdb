package com.softvision.database.dao

import androidx.room.*
import com.softvision.database.model.*
import com.softvision.database.model.MovieResultCrossRef
import com.softvision.database.model.MovieResultEntity
import com.softvision.database.model.TvShowResultCrossRef
import com.softvision.database.model.TvShowResultEntity
import com.softvision.database.model.TvShowResultsWithTvShows
import com.softvision.model.media.SortType
import org.jetbrains.annotations.TestOnly

@Dao
internal interface ResultsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveMovieResult(result: MovieResultEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveTvShowResult(result: TvShowResultEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveMovieResultCrossRef(crossRef: MovieResultCrossRef)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun saveTvShowResultCrossRef(crossRef: TvShowResultCrossRef)

    @Query("DELETE FROM movie_results")
    suspend  fun clearMovieResults()

    @Query("DELETE FROM tv_show_results")
    suspend  fun clearTvShowResults()

    @Transaction
    @Query("SELECT * FROM movie_results WHERE page = :page AND sortType = :sortType")
    suspend fun getMovieResultWithMovies(page: Int, sortType: SortType): MovieResultsWithMovies

    @Transaction
    @Query("SELECT * FROM movie_results WHERE page = :page AND genreId = :genre")
    suspend fun getMovieResultWithMovies(page: Int, genre: Long): MovieResultsWithMovies

    @Transaction
    @Query("SELECT * FROM tv_show_results WHERE page = :page AND sortType = :sortType")
    suspend fun getTvShowResultWithTvShows(page: Int, sortType: SortType): TvShowResultsWithTvShows

    @Transaction
    @Query("SELECT * FROM tv_show_results WHERE page = :page AND genreId = :genre")
    suspend fun getTvShowResultWithTvShows(page: Int, genre: Long): TvShowResultsWithTvShows

    @TestOnly
    @Query("SELECT * FROM movie_results")
    suspend fun getMovieResults(): MovieResultEntity

    @TestOnly
    @Query("SELECT * FROM tv_show_results")
    suspend fun getTvShowResults(): TvShowResultEntity

    @TestOnly
    @Query("SELECT * FROM movie_result_cross_ref")
    suspend fun getMovieResultCrossRef(): MovieResultCrossRef

    @TestOnly
    @Query("SELECT * FROM tv_show_result_cross_ref")
    suspend fun getTvShowResultCrossRef(): TvShowResultCrossRef
}