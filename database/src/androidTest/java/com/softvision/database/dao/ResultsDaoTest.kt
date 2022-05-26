package com.softvision.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.vacxe.konveyor.base.randomBuild
import com.softvision.database.TmdbDatabase
import com.softvision.database.model.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ResultsDaoTest {

    private lateinit var movieDatabase: TmdbDatabase
    private lateinit var resultsDao: ResultsDao
    private lateinit var movieDao: MovieDao
    private lateinit var tvShowDao: TvShowDao


    private val movieResultEntity: MovieResultEntity = randomBuild()
    private val tvShowResultEntity: TvShowResultEntity = randomBuild()
    private val movieResultCrossRef: MovieResultCrossRef = randomBuild()
    private val tvShowResultCrossRef: TvShowResultCrossRef = randomBuild()
    private val movieEntities: List<MovieEntity> = (1..10).map { randomBuild() }
    private val tvShowEntities: List<TvShowEntity> = (1..10).map { randomBuild() }

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        movieDatabase = Room.inMemoryDatabaseBuilder(context, TmdbDatabase::class.java).build()
        resultsDao = movieDatabase.resultsDao()
        movieDao = movieDatabase.movieDao()
        tvShowDao = movieDatabase.tvShowsDao()
    }

    @After
    fun teardown() = movieDatabase.close()

    @Test
    fun saveMovieResult_writes_correctly_into_table() = runTest {
        resultsDao.saveMovieResult(movieResultEntity)
        val result = resultsDao.getMovieResults()
        Assert.assertEquals(movieResultEntity, result)
    }

    @Test
    fun saveTvShowResult_writes_correctly_into_table() = runTest {
        resultsDao.saveTvShowResult(tvShowResultEntity)
        val result = resultsDao.getTvShowResults()
        Assert.assertEquals(tvShowResultEntity, result)
    }

    @Test
    fun saveMovieResultCrossRef_writes_correctly_into_table() = runTest {
        resultsDao.saveMovieResultCrossRef(movieResultCrossRef)
        val result = resultsDao.getMovieResultCrossRef()
        Assert.assertEquals(movieResultCrossRef, result)
    }

    @Test
    fun saveTvShowResultCrossRef_writes_correctly_into_table() = runTest {
        resultsDao.saveTvShowResultCrossRef(tvShowResultCrossRef)
        val result = resultsDao.getTvShowResultCrossRef()
        Assert.assertEquals(tvShowResultCrossRef, result)
    }

    @Test
    fun clearMovieResults_clears_the_table() = runTest {
        resultsDao.saveMovieResult(movieResultEntity)
        resultsDao.clearMovieResults()
        val result = resultsDao.getMovieResults()
        Assert.assertEquals(result, null)
    }

    @Test
    fun clearTvShowResults_clears_the_table() = runTest {
        resultsDao.saveTvShowResult(tvShowResultEntity)
        resultsDao.clearTvShowResults()
        val result = resultsDao.getTvShowResults()
        Assert.assertEquals(result, null)
    }

    @Test
    fun getMovieResultWithMovies_returns_movies_filtered_by_page_and_sort_type() = runTest {
        movieDao.saveMovies(movieEntities)
        resultsDao.saveMovieResult(movieResultEntity)
        movieEntities.forEach { movie ->
            resultsDao.saveMovieResultCrossRef(MovieResultCrossRef(movie.itemId, movieResultEntity.resultId.toLong()))
        }
        val result = resultsDao.getMovieResultWithMovies(movieResultEntity.page, movieResultEntity.sortType!!)
        Assert.assertEquals(movieResultEntity, result.result)
        Assert.assertEquals(movieEntities, result.movies)
    }

    @Test
    fun getMovieResultWithMovies_returns_movies_filtered_by_page_and_genre() = runTest {
        movieDao.saveMovies(movieEntities)
        resultsDao.saveMovieResult(movieResultEntity)
        movieEntities.forEach { movie ->
            resultsDao.saveMovieResultCrossRef(MovieResultCrossRef(movie.itemId, movieResultEntity.resultId.toLong()))
        }
        val result = resultsDao.getMovieResultWithMovies(movieResultEntity.page, movieResultEntity.genreId!!)
        Assert.assertEquals(movieResultEntity, result.result)
        Assert.assertEquals(movieEntities, result.movies)
    }

    @Test
    fun getTvShowResultWithTvShows_returns_TV_shows_filtered_by_page_and_sort_type() = runTest {
        tvShowDao.saveTvShows(tvShowEntities)
        resultsDao.saveTvShowResult(tvShowResultEntity)
        tvShowEntities.forEach { tvShow ->
            resultsDao.saveTvShowResultCrossRef(TvShowResultCrossRef(tvShow.itemId, tvShowResultEntity.resultId.toLong()))
        }
        val result = resultsDao.getTvShowResultWithTvShows(tvShowResultEntity.page, tvShowResultEntity.sortType!!)
        Assert.assertEquals(tvShowResultEntity, result.result)
        Assert.assertEquals(tvShowEntities, result.tvShows)
    }

    @Test
    fun getTvShowResultWithTvShow_returns_TV_shows_filtered_by_page_and_genre() = runTest {
        tvShowDao.saveTvShows(tvShowEntities)
        resultsDao.saveTvShowResult(tvShowResultEntity)
        tvShowEntities.forEach { tvShow ->
            resultsDao.saveTvShowResultCrossRef(TvShowResultCrossRef(tvShow.itemId, tvShowResultEntity.resultId.toLong()))
        }
        val result = resultsDao.getTvShowResultWithTvShows(tvShowResultEntity.page, tvShowResultEntity.genreId!!)
        Assert.assertEquals(tvShowResultEntity, result.result)
        Assert.assertEquals(tvShowEntities, result.tvShows)
    }

}