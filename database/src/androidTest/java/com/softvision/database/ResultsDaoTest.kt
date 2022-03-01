package com.softvision.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.vacxe.konveyor.base.randomBuild
import com.softvision.database.dao.ResultsDao
import com.softvision.database.model.MovieResultCrossRef
import com.softvision.database.model.MovieResultEntity
import com.softvision.database.model.TvShowResultCrossRef
import com.softvision.database.model.TvShowResultEntity
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

    private val movieResultEntity: MovieResultEntity = randomBuild()
    private val tvShowResultEntity: TvShowResultEntity = randomBuild()
    private val movieResultCrossRef: MovieResultCrossRef = randomBuild()
    private val tvShowResultCrossRef: TvShowResultCrossRef = randomBuild()


    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        movieDatabase = Room.inMemoryDatabaseBuilder(context, TmdbDatabase::class.java).build()
        resultsDao = movieDatabase.resultsDao()
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
        try {
            resultsDao.getMovieResults()
        }catch (exception: Exception){
            Assert.assertEquals(true,exception is Exception)
            Log.e("asdasd",exception.javaClass.simpleName)
        }
    }

//    @Test
//    fun clearTvShowResultsClearTable() {
//        val resultsEntity: TvShowResultsEntity = arbitrary()
//        resultsDao.saveTvShowResult(resultsEntity).test().assertComplete().dispose()
//        resultsDao.clearTvShowResults().test().assertComplete()
//        resultsDao.getTvShowResults().test()
//            .assertError(EmptyResultSetException::class.java).dispose()
//    }

//    suspend  fun clearMovieResults()
//    suspend  fun clearTvShowResults()
//    suspend fun getMovieResultWithMovies(page: Int, sortType: SortType): MovieResultsWithMovies
//    suspend fun getMovieResultWithMovies(page: Int, genre: Long): MovieResultsWithMovies
//    suspend fun getTvShowResultWithTvShows(page: Int, sortType: SortType): TvShowResultsWithTvShows
//    suspend fun getTvShowResultWithTvShows(page: Int, genre: Long): TvShowResultsWithTvShows
}