package com.softvision.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.vacxe.konveyor.base.randomBuild
import com.softvision.database.dao.MovieDao
import com.softvision.database.model.MovieEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class MovieDaoTest {

    private lateinit var movieDatabase: TmdbDatabase
    private lateinit var movieDao: MovieDao

    private val movies: List<MovieEntity> = (1..10).map { randomBuild() }

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        movieDatabase = Room.inMemoryDatabaseBuilder(context, TmdbDatabase::class.java).build()
        movieDao = movieDatabase.movieDao()
    }

    @After
    fun teardown() = movieDatabase.close()

    @Test
    fun saveMovies_writes_correctly_in_database() = runTest {
        movieDao.saveMovies(movies)
        val moviesInDatabase = movieDao.getMovies()
        Assert.assertEquals(true, moviesInDatabase.containsAll(movies))
    }

    @Test
    fun clearMovies_clears_the_movie_table() = runTest {
        movieDao.saveMovies(movies)
        movieDao.clearMovies()
        val moviesInDatabase = movieDao.getMovies()
        Assert.assertEquals(true, moviesInDatabase.isEmpty())
    }
}