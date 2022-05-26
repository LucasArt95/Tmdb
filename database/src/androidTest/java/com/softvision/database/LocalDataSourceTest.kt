package com.softvision.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.github.vacxe.konveyor.base.randomBuild
import com.softvision.database.dao.GenresDao
import com.softvision.database.dao.MovieDao
import com.softvision.database.dao.ResultsDao
import com.softvision.database.dao.TvShowDao
import com.softvision.model.account.AccountDetails
import com.softvision.model.genre.Genre
import com.softvision.model.genre.GenreResult
import com.softvision.model.media.*
import com.softvision.preferences.TmdbSharedPreferences
import com.softvision.preferences.TmdbSharedPreferences.TmdbPreferenceKey.ACCOUNT_DETAILS
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

@ExperimentalCoroutinesApi
class LocalDataSourceTest {

    private lateinit var tmdbDatabase: TmdbDatabase
    private lateinit var movieDao: MovieDao
    private lateinit var tvShowDao: TvShowDao
    private lateinit var resultsDao: ResultsDao
    private lateinit var genresDao: GenresDao

    @MockK
    private lateinit var tmdbSharedPreferences: TmdbSharedPreferences

    private lateinit var localDataSource: LocalDataSource

    private val movies: List<Movie> = (1..10).map { randomBuild() }
    private val tvShows: List<TvShow> = (1..10).map { randomBuild() }
    private val movieResult = ItemResult(1, movies, 10, 1)
    private val tvShowResult = ItemResult(1, tvShows, 10, 1)
    private val sortType = SortType.values().random()
    private val mediaType = MediaType.values().random()
    private val genreId = Random.nextLong()
    private val genres: List<Genre> = (1..10).map { randomBuild() }
    private val genreResult = GenreResult(genres)

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        val context = ApplicationProvider.getApplicationContext<Context>()
        tmdbDatabase = Room.inMemoryDatabaseBuilder(context, TmdbDatabase::class.java).build()

        movieDao = tmdbDatabase.movieDao()
        tvShowDao = tmdbDatabase.tvShowsDao()
        resultsDao = tmdbDatabase.resultsDao()
        genresDao = tmdbDatabase.genresDao()

        localDataSource = LocalDataSource(movieDao, tvShowDao, genresDao, resultsDao, tmdbSharedPreferences)
    }

    @After
    fun teardown() = tmdbDatabase.close()

    @Test
    fun getMovies_returns_movies_by_sort_type() = runTest {
        localDataSource.saveMovies(movieResult, sortType).join()
        val result = localDataSource.getMovies(movieResult.page, sortType)
        Assert.assertEquals(movieResult, result)
    }

    @Test
    fun getMovies_returns_movies_by_genre_ID() = runTest {
        localDataSource.saveMovies(movieResult, genreId).join()
        val result = localDataSource.getMovies(movieResult.page, genreId)
        Assert.assertEquals(movieResult, result)
    }


    @Test
    fun getTvShows_returns_movies_by_sort_type() = runTest {
        localDataSource.saveTvShows(tvShowResult, sortType).join()
        val result = localDataSource.getTvShows(tvShowResult.page, sortType)
        Assert.assertEquals(tvShowResult, result)
    }

    @Test
    fun getTvShows_returns_movies_by_genre_ID() = runTest {
        localDataSource.saveTvShows(tvShowResult, genreId).join()
        val result = localDataSource.getTvShows(tvShowResult.page, genreId)
        Assert.assertEquals(tvShowResult, result)
    }

    @Test
    fun getMovieGenres_returns_list_of_movie_genres() = runTest {
        localDataSource.saveGenres(genreResult.genres, MediaType.MOVIE).join()
        localDataSource.saveGenres((1..10).map { randomBuild() }, MediaType.TV_SHOW).join()
        val result = localDataSource.getMovieGenres()
        assert(result.all { genres.contains(it) })
    }

    @Test
    fun getMovieGenres_returns_list_of_movie_genres_filtered_by_ID() = runTest {
        val lowerBound = (1..(genreResult.genres.size / 2)).random()
        val upperBound = ((genreResult.genres.size / 2) until genreResult.genres.size).random()
        val expectedGenres = genreResult.genres.shuffled().slice(lowerBound..upperBound)
        val genresIds = expectedGenres.map { it.id }
        localDataSource.saveGenres(genreResult.genres, MediaType.MOVIE).join()
        localDataSource.saveGenres((1..10).map { randomBuild() }, MediaType.TV_SHOW).join()
        val result = localDataSource.getMovieGenres(genresIds)
        assert(result.all { genres.contains(it) })
    }

    @Test
    fun getTvShowGenres_returns_list_of_TV_shows_genres() = runTest {
        localDataSource.saveGenres(genreResult.genres, MediaType.TV_SHOW).join()
        localDataSource.saveGenres((1..10).map { randomBuild() }, MediaType.MOVIE).join()
        val result = localDataSource.getTvShowGenres()
        assert(result.all { genres.contains(it) })
    }

    @Test
    fun getTvShowGenres_returns_list_of_TV_show_genres_filtered_by_ID() = runTest {
        val lowerBound = (1..(genreResult.genres.size / 2)).random()
        val upperBound = ((genreResult.genres.size / 2) until genreResult.genres.size).random()
        val expectedGenres = genreResult.genres.shuffled().slice(lowerBound..upperBound)
        val genresIds = expectedGenres.map { it.id }

        localDataSource.saveGenres(genreResult.genres, MediaType.TV_SHOW).join()
        localDataSource.saveGenres((1..10).map { randomBuild() }, MediaType.MOVIE).join()
        val result = localDataSource.getTvShowGenres(genresIds)
        assert(result.all { genres.contains(it) })
    }

    @Test
    fun getAccountDetailsReturnsTheSameDataSavedPreviously() = runTest {
        val accountDetails: AccountDetails = randomBuild()
        localDataSource.saveAccountDetails(accountDetails)
        val result = localDataSource.getAccountDetails()
        Assert.assertEquals(accountDetails, result)
    }

    @Test
    fun saveMoviesWithSortTypeWritesIntoDatabase() = runTest {
        localDataSource.saveMovies(movieResult, sortType).join()
        val result = resultsDao.getMovieResultWithMovies(movieResult.page, sortType)
        Assert.assertEquals(movieResult, ItemResult(result.result.page, result.movies.toMovies(), result.result.totalResults, result.result.totalPages))
    }

    @Test
    fun saveMoviesWithGenreIdWritesIntoDatabase() = runTest {
        localDataSource.saveMovies(movieResult, genreId).join()
        val result = resultsDao.getMovieResultWithMovies(movieResult.page, genreId)
        Assert.assertEquals(movieResult, ItemResult(result.result.page, result.movies.toMovies(), result.result.totalResults, result.result.totalPages))
    }

    @Test
    fun saveTvShowsWithSortTypeWritesIntoDatabase() = runTest {
        localDataSource.saveTvShows(tvShowResult, sortType).join()
        val result = resultsDao.getTvShowResultWithTvShows(tvShowResult.page, sortType)
        Assert.assertEquals(tvShowResult, ItemResult(result.result.page, result.tvShows.toTvShows(), result.result.totalResults, result.result.totalPages))
    }


    @Test
    fun saveTvShowsWithGenreIdWritesIntoDatabase() = runTest {
        localDataSource.saveTvShows(tvShowResult, genreId).join()
        val result = resultsDao.getTvShowResultWithTvShows(tvShowResult.page, genreId)
        Assert.assertEquals(tvShowResult, ItemResult(result.result.page, result.tvShows.toTvShows(), result.result.totalResults, result.result.totalPages))
    }

    @Test
    fun saveGenresWritesIntoGenresTable() = runTest {
        localDataSource.saveGenres(genreResult.genres, mediaType)
        val genreEntities = genresDao.getGenres()
        assert(genreResult.genres.all { entity -> genreEntities.any { it.id == entity.id } })
    }

    @Test
    fun saveAccountDetailWritesIntoSharedPreferences() = runTest {
        val accountDetails: AccountDetails = randomBuild()
        localDataSource.saveAccountDetails(accountDetails)
        val savedAccountDetails = tmdbSharedPreferences.getPreference<AccountDetails>(ACCOUNT_DETAILS)
        Assert.assertEquals(accountDetails, savedAccountDetails)
    }
}