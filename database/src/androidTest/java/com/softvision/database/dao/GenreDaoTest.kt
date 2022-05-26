package com.softvision.database.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.vacxe.konveyor.base.randomBuild
import com.softvision.database.TmdbDatabase
import com.softvision.database.model.GenreEntity
import com.softvision.model.media.MediaType
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class GenreDaoTest {

    private lateinit var movieDatabase: TmdbDatabase
    private lateinit var genresDao: GenresDao

    private val genres: List<GenreEntity> = (1..10).map { randomBuild() }
    private val mediaType = MediaType.values().random()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        movieDatabase = Room.inMemoryDatabaseBuilder(context, TmdbDatabase::class.java).build()
        genresDao = movieDatabase.genresDao()
    }

    @After
    fun teardown() = movieDatabase.close()

    @Test
    fun saveGenres_writes_correctly_in_database() = runTest {
        genresDao.saveGenres(genres)

        val genresInDatabase = genresDao.getGenres()
        Assert.assertEquals(true, genresInDatabase.containsAll(genres))
    }

    @Test
    fun getGenres_filters_correctly_by_media_type() = runTest {
        val filteredGenres = genres.filter { it.mediaType == mediaType }

        genresDao.saveGenres(genres)

        val genresInDatabase = genresDao.getGenres(mediaType)
        Assert.assertEquals(true, genresInDatabase.containsAll(filteredGenres))
    }

    @Test
    fun getGenres_filters_correctly_by_media_type_and_id() = runTest {
        val lowerBound = (1..(genres.size / 2)).random()
        val upperBound = ((genres.size / 2) until genres.size).random()
        val genresIds = genres.shuffled().slice(lowerBound..upperBound).map { it.id }
        val filteredGenres = genres.filter { it.mediaType == mediaType && it.id in genresIds }

        genresDao.saveGenres(genres)

        val genresInDatabase = genresDao.getGenres(genresIds, mediaType)
        Assert.assertEquals(true, genresInDatabase.containsAll(filteredGenres))
    }
}