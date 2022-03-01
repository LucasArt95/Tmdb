package com.softvision.domain

import com.github.vacxe.konveyor.base.randomBuild
import com.softvision.database.LocalDataSource
import com.softvision.model.account.AccountDetails
import com.softvision.model.genre.Genre
import com.softvision.model.genre.GenreResult
import com.softvision.model.media.*
import com.softvision.network.RemoteDataSource
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

@ExperimentalCoroutinesApi
class MovieRepositoryImplTest {

    private val page = Random.nextInt()
    private val movies: List<Movie> = (1..10).map { randomBuild() }
    private val movieResult = ItemResult(page, movies, movies.size, 5)

    private val tvShows: List<TvShow> = (1..10).map { randomBuild() }
    private val tvShowsResult = ItemResult(page, tvShows, tvShows.size, 5)

    private val genreResult = randomBuild<GenreResult>().copy((1..10).map { randomBuild() })
    private val lowerBound = (1..(genreResult.genres.size / 2)).random()
    private val upperBound = ((genreResult.genres.size / 2) until genreResult.genres.size).random()
    private val requestedGenres = genreResult.genres.shuffled().slice(lowerBound..upperBound).map { it.id }
    private val expectedGenres = genreResult.genres.filter { it.id in requestedGenres }

    private val accountDetails: AccountDetails = randomBuild()

    @MockK
    private lateinit var localDataSource: LocalDataSource

    @MockK
    private lateinit var remoteDataSource: RemoteDataSource

    private lateinit var movieRepository: MovieRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        movieRepository = MovieRepositoryImpl(remoteDataSource, localDataSource)

    }

    @Test
    fun `getCategory gets data from local and doesn't request from remote`() = runTest {
        val sortType = SortType.values().random()
        coEvery { localDataSource.getMovies(any(), any<SortType>()) } returns movieResult
        coEvery { localDataSource.getTvShows(any(), any<SortType>()) } returns tvShowsResult
        val expectedResult = ItemResult(
            page = page,
            results = movies + tvShows,
            totalResults = movieResult.totalResults + tvShowsResult.totalResults,
            totalPages = maxOf(movieResult.totalPages, tvShowsResult.totalPages)
        )

        val result = movieRepository.getCategory(page, sortType)

        coVerify(inverse = true) { remoteDataSource.getMovies(any(), any<SortType>()) }
        coVerify(inverse = true) { remoteDataSource.getTvShows(any(), any<SortType>()) }
        Assert.assertEquals(expectedResult, result)
    }

    @Test
    fun `getCategory gets data from remote and saves it locally when local doesn't have it`() = runTest {
        val sortType = SortType.values().random()
        coEvery { localDataSource.getMovies(any(), any<SortType>()) } throws Exception("Test Exception")
        coEvery { localDataSource.saveMovies(any(), any<SortType>()) } returns Unit
        coEvery { localDataSource.saveTvShows(any(), any<SortType>()) } returns Unit
        coEvery { remoteDataSource.getMovies(any(), any<SortType>()) } returns movieResult
        coEvery { remoteDataSource.getTvShows(any(), any<SortType>()) } returns tvShowsResult
        val expectedResult = ItemResult(
            page = page,
            results = movies + tvShows,
            totalResults = movieResult.totalResults + tvShowsResult.totalResults,
            totalPages = maxOf(movieResult.totalPages, tvShowsResult.totalPages)
        )

        val result = movieRepository.getCategory(page, sortType)

        coVerify { localDataSource.saveMovies(movieResult, sortType) }
        coVerify { localDataSource.saveTvShows(tvShowsResult, sortType) }
        Assert.assertEquals(expectedResult, result)
    }

    @Test
    fun `getMovies by sort type gets data from local and doesn't request from remote`() = runTest {
        val sortType = SortType.values().random()
        coEvery { localDataSource.getMovies(any(), any<SortType>()) } returns movieResult

        val result = movieRepository.getMovies(page, sortType)

        coVerify(inverse = true) { remoteDataSource.getMovies(any(), any<SortType>()) }
        Assert.assertEquals(movieResult, result)
    }

    @Test
    fun `getMovies by sort type gets data from remote and saves it locally when local doesn't have it`() = runTest {
        val sortType = SortType.values().random()
        coEvery { localDataSource.getMovies(any(), any<SortType>()) } throws Exception("Test Exception")
        coEvery { localDataSource.saveMovies(any(), any<SortType>()) } returns Unit
        coEvery { remoteDataSource.getMovies(any(), any<SortType>()) } returns movieResult

        val result = movieRepository.getMovies(page, sortType)

        coVerify { localDataSource.saveMovies(movieResult, sortType) }
        Assert.assertEquals(movieResult, result)
    }

    @Test
    fun `getMovies by genre gets data from local and doesn't request from remote`() = runTest {
        val genre: Genre = randomBuild()
        coEvery { localDataSource.getMovies(any(), any<Long>()) } returns movieResult

        val result = movieRepository.getMovies(page, genre)

        coVerify(inverse = true) { remoteDataSource.getMovies(any(), any<Long>()) }
        Assert.assertEquals(movieResult, result)
    }

    @Test
    fun `getMovies by genre gets data from remote and saves it locally when local doesn't have it`() = runTest {
        val genre: Genre = randomBuild()
        coEvery { localDataSource.getMovies(any(), any<Long>()) } throws Exception("Test Exception")
        coEvery { localDataSource.saveMovies(any(), any<Long>()) } returns Unit
        coEvery { remoteDataSource.getMovies(any(), any<Long>()) } returns movieResult

        val result = movieRepository.getMovies(page, genre)

        coVerify { localDataSource.saveMovies(movieResult, genre.id) }
        Assert.assertEquals(movieResult, result)
    }

    @Test
    fun `getTvShows by sort type gets data from local and doesn't request from remote`() = runTest {
        val sortType = SortType.values().random()
        coEvery { localDataSource.getTvShows(any(), any<SortType>()) } returns tvShowsResult

        val result = movieRepository.getTvShows(page, sortType)

        coVerify(inverse = true) { remoteDataSource.getTvShows(any(), any<SortType>()) }
        Assert.assertEquals(tvShowsResult, result)
    }

    @Test
    fun `getTvShows by sort type gets data from remote and saves it locally when local doesn't have it`() = runTest {
        val sortType = SortType.values().random()
        coEvery { localDataSource.getTvShows(any(), any<SortType>()) } throws Exception("Test Exception")
        coEvery { localDataSource.saveTvShows(any(), any<SortType>()) } returns Unit
        coEvery { remoteDataSource.getTvShows(any(), any<SortType>()) } returns tvShowsResult

        val result = movieRepository.getTvShows(page, sortType)

        coVerify { localDataSource.saveTvShows(tvShowsResult, sortType) }
        Assert.assertEquals(tvShowsResult, result)
    }

    @Test
    fun `getTvShows by genre gets data from local and doesn't request from remote`() = runTest {
        val genre: Genre = randomBuild()
        coEvery { localDataSource.getTvShows(any(), any<Long>()) } returns tvShowsResult

        val result = movieRepository.getTvShows(page, genre)

        coVerify(inverse = true) { remoteDataSource.getTvShows(any(), any<Long>()) }
        Assert.assertEquals(tvShowsResult, result)
    }

    @Test
    fun `getTvShows by genre gets data from remote and saves it locally when local doesn't have it`() = runTest {
        val genre: Genre = randomBuild()
        coEvery { localDataSource.getTvShows(any(), any<Long>()) } throws Exception("Test Exception")
        coEvery { localDataSource.saveTvShows(any(), any<Long>()) } returns Unit
        coEvery { remoteDataSource.getTvShows(any(), any<Long>()) } returns tvShowsResult

        val result = movieRepository.getTvShows(page, genre)

        coVerify { localDataSource.saveTvShows(tvShowsResult, genre.id) }
        Assert.assertEquals(tvShowsResult, result)
    }

    @Test
    fun `getMovieGenres gets data from local and doesn't request from remote`() = runTest {
        coEvery { localDataSource.getMovieGenres(any()) } returns expectedGenres

        val result = movieRepository.getMovieGenres(requestedGenres)

        coVerify(inverse = true) { remoteDataSource.getMovieGenres(requestedGenres) }
        Assert.assertEquals(true, result.containsAll(expectedGenres))
    }

    @Test
    fun `getMovieGenres gets data from remote and saves it locally when local doesn't have it`() = runTest {
        coEvery { localDataSource.getMovieGenres(any()) } throws Exception("Test Exception")
        coEvery { localDataSource.saveGenres(any(), any()) } returns Unit
        coEvery { remoteDataSource.getMovieGenres(any()) } returns expectedGenres

        val result = movieRepository.getMovieGenres(requestedGenres)

        coVerify { localDataSource.saveGenres(expectedGenres, MediaType.MOVIE) }
        Assert.assertEquals(true, result.containsAll(expectedGenres))
    }

    @Test
    fun `getTvShowGenres gets data from local and doesn't request from remote`() = runTest {
        coEvery { localDataSource.getTvShowGenres(any()) } returns expectedGenres

        val result = movieRepository.getTvShowGenres(requestedGenres)

        coVerify(inverse = true) { remoteDataSource.getTvShowGenres(requestedGenres) }
        Assert.assertEquals(true, result.containsAll(expectedGenres))
    }

    @Test
    fun `getTvShowGenres gets data from remote and saves it locally when local doesn't have it`() = runTest {
        coEvery { localDataSource.getTvShowGenres(any()) } throws Exception("Test Exception")
        coEvery { localDataSource.saveGenres(any(), any()) } returns Unit
        coEvery { remoteDataSource.getTvShowGenres(any()) } returns expectedGenres

        val result = movieRepository.getTvShowGenres(requestedGenres)

        coVerify { localDataSource.saveGenres(expectedGenres, MediaType.TV_SHOW) }
        Assert.assertEquals(true, result.containsAll(expectedGenres))
    }


    @Test
    fun `getAccountDetails gets data from local and doesn't request from remote`() = runTest {
        coEvery { localDataSource.getAccountDetails() } returns accountDetails

        val result = movieRepository.getAccountDetails()

        coVerify(inverse = true) { remoteDataSource.getTvShowGenres(requestedGenres) }
        Assert.assertEquals(accountDetails, result)
    }

    @Test
    fun `getAccountDetails gets data from remote and saves it locally when local doesn't have it`() = runTest {
        coEvery { localDataSource.getAccountDetails() } throws Exception("Test Exception")
        coEvery { localDataSource.saveAccountDetails(any()) } returns Unit
        coEvery { remoteDataSource.getAccountDetails() } returns accountDetails

        val result = movieRepository.getAccountDetails()

        coVerify { localDataSource.saveAccountDetails(accountDetails) }
        Assert.assertEquals(accountDetails, result)
    }
}