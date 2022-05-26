package com.softvision.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.vacxe.konveyor.base.randomBuild
import com.softvision.database.TmdbDatabase
import com.softvision.database.model.TvShowEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class TvShowDaoTest {

    private lateinit var movieDatabase: TmdbDatabase
    private lateinit var tvShowDao: TvShowDao

    private val tvShows: List<TvShowEntity> = (1..10).map { randomBuild() }

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        movieDatabase = Room.inMemoryDatabaseBuilder(context, TmdbDatabase::class.java).build()
        tvShowDao = movieDatabase.tvShowsDao()
    }

    @After
    fun teardown() = movieDatabase.close()

    @Test
    fun saveTvShows_writes_correctly_in_database() = runTest {
        tvShowDao.saveTvShows(tvShows)
        val tvShowsInDatabase = tvShowDao.getTvShows()
        Assert.assertEquals(true, tvShowsInDatabase.containsAll(tvShows))
    }

    @Test
    fun clearTvShows_clears_the_movie_table() = runTest {
        tvShowDao.saveTvShows(tvShows)
        tvShowDao.clearTvShows()
        val tvShowsInDatabase = tvShowDao.getTvShows()
        Assert.assertEquals(true, tvShowsInDatabase.isEmpty())
    }
}