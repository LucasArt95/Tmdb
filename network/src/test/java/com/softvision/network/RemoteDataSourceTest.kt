package com.softvision.network

import com.github.vacxe.konveyor.base.randomBuild
import com.softvision.model.account.AccountDetails
import com.softvision.model.genre.GenreResult
import com.softvision.model.media.*
import com.softvision.network.model.FavoriteItem
import com.softvision.network.model.FavoriteRequestData
import com.softvision.network.service.TmdbService
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.random.Random

@ExperimentalCoroutinesApi
class RemoteDataSourceTest {

    private val page = Random.nextInt()
    private val movies: List<Movie> = (1..10).map { randomBuild() }
    private val movieResult = randomBuild<ItemResult<Movie>>().copy(results = movies)


    private val tvShows: List<TvShow> = (1..10).map { randomBuild() }
    private val tvShowsResult = randomBuild<ItemResult<TvShow>>().copy(results = tvShows)

    @MockK
    private lateinit var tmdbService: TmdbService
    private lateinit var remoteDataSource: RemoteDataSource

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        remoteDataSource = RemoteDataSource(tmdbService)

        coEvery { tmdbService.getPopularMovies(any()) } returns movieResult
        coEvery { tmdbService.getTopRatedMovies(any()) } returns movieResult
        coEvery { tmdbService.getTrendingMovies(any()) } returns movieResult
        coEvery { tmdbService.getUpcomingMovies(any(), any()) } returns movieResult
        coEvery { tmdbService.getNowPlayingMovies(any()) } returns movieResult

        coEvery { tmdbService.getPopularTvShows(any()) } returns tvShowsResult
        coEvery { tmdbService.getTopRatedTvShows(any()) } returns tvShowsResult
        coEvery { tmdbService.getTrendingTvShows(any()) } returns tvShowsResult
        coEvery { tmdbService.getUpcomingTvShows(any(), any()) } returns tvShowsResult
        coEvery { tmdbService.getOnTheAirTvShows(any()) } returns tvShowsResult
    }

    @Test
    fun `getMovies returns correct type of request`() = runTest {
        SortType.values().forEach {
            remoteDataSource.getMovies(page, it)
            when (it) {
                SortType.POPULAR -> coVerify { tmdbService.getPopularMovies(page) }
                SortType.TOP_RATED -> coVerify { tmdbService.getTopRatedMovies(page) }
                SortType.TRENDING -> coVerify { tmdbService.getTrendingMovies(page) }
                SortType.UPCOMING -> coVerify { tmdbService.getUpcomingMovies(eq(page), any()) }
                SortType.NOW_PLAYING -> coVerify { tmdbService.getNowPlayingMovies(page) }
            }
        }
    }

    @Test
    fun `getMovieGenres returns correct type of request and filters data`() = runTest {
        val genreResult = randomBuild<GenreResult>().copy((1..10).map { randomBuild() })
        val lowerBound = (1..(genreResult.genres.size / 2)).random()
        val upperBound = ((genreResult.genres.size / 2) until genreResult.genres.size).random()
        val requestedGenres = genreResult.genres.shuffled().slice(lowerBound..upperBound).map { it.id }
        val expectedGenres = genreResult.genres.filter { it.id in requestedGenres }
        coEvery { tmdbService.getMovieGenres() } returns genreResult

        val result = remoteDataSource.getMovieGenres(requestedGenres)

        Assert.assertEquals(expectedGenres, result)
        coVerify { tmdbService.getMovieGenres() }
    }

    @Test
    fun `getMovies by genres returns correct type of request`() = runTest {
        val genreId = Random.nextLong()
        coEvery { tmdbService.getMoviesByGenres(any(), any()) } returns movieResult

        val result = remoteDataSource.getMovies(page, genreId)

        Assert.assertEquals(movieResult, result)
        coVerify { tmdbService.getMoviesByGenres(page, listOf(genreId)) }
    }

    @Test
    fun `getTvShows returns correct type of request`() = runTest {
        SortType.values().forEach {
            remoteDataSource.getTvShows(page, it)
            when (it) {
                SortType.POPULAR -> coVerify { tmdbService.getPopularTvShows(page) }
                SortType.TOP_RATED -> coVerify { tmdbService.getTopRatedTvShows(page) }
                SortType.TRENDING -> coVerify { tmdbService.getTrendingTvShows(page) }
                SortType.UPCOMING -> coVerify { tmdbService.getUpcomingTvShows(eq(page), any()) }
                SortType.NOW_PLAYING -> coVerify { tmdbService.getOnTheAirTvShows(page) }
            }
        }
    }

    @Test
    fun `getTvShowGenres returns correct type of request and filters data`() = runTest {
        val genreResult = randomBuild<GenreResult>().copy((1..10).map { randomBuild() })
        val lowerBound = (1..(genreResult.genres.size / 2)).random()
        val upperBound = ((genreResult.genres.size / 2) until genreResult.genres.size).random()
        val requestedGenres = genreResult.genres.shuffled().slice(lowerBound..upperBound).map { it.id }
        val expectedGenres = genreResult.genres.filter { it.id in requestedGenres }
        coEvery { tmdbService.getTvShowGenres() } returns genreResult

        val result = remoteDataSource.getTvShowGenres(requestedGenres)

        Assert.assertEquals(true, result.containsAll(expectedGenres))
        coVerify { tmdbService.getTvShowGenres() }
    }

    @Test
    fun `getTvShows by genres returns correct type of request`() = runTest {
        val genreId = Random.nextLong()
        coEvery { tmdbService.getTvShowsByGenres(any(), any()) } returns tvShowsResult

        val result = remoteDataSource.getTvShows(page, genreId)

        Assert.assertEquals(tvShowsResult, result)
        coVerify { tmdbService.getTvShowsByGenres(page, listOf(genreId)) }
    }

    @Test
    fun `search returns correct type of request`() = runTest {
        val query = UUID.randomUUID().toString()
        val itemResult: ItemResult<TmdbItem> = movieResult as ItemResult<TmdbItem>
        coEvery { tmdbService.search(any(), any()) } returns itemResult

        val result = remoteDataSource.search(page, query)

        Assert.assertEquals(itemResult, result)
        coVerify { tmdbService.search(query, page) }
    }

    @Test
    fun `getAccountDetails returns correct type of request`() = runTest {
        val accountDetails: AccountDetails = randomBuild()
        coEvery { tmdbService.getAccountDetails() } returns accountDetails

        val result = remoteDataSource.getAccountDetails()

        Assert.assertEquals(accountDetails, result)
        coVerify { tmdbService.getAccountDetails() }
    }

    @Test
    fun `getFavoriteItems returns correct type of request and combines data`() = runTest {
        coEvery { tmdbService.getFavoriteMovies(any(), any()) } returns movieResult.copy(page = page, totalResults = movies.size, totalPages = 1)
        coEvery { tmdbService.getFavoriteTvShows(any(), any()) } returns tvShowsResult.copy(page = page, totalResults = tvShows.size, totalPages = 1)
        val accountId = Random.nextInt()
        val expectedValue = ItemResult(page, movies + tvShows, movies.size + tvShows.size, 1)

        val result = remoteDataSource.getFavoriteItems(accountId, page)

        Assert.assertEquals(expectedValue, result)
    }

    @Test
    fun `setFavorite returns correct type of request`() = runTest {
        val mediaType = MediaType.values().random()
        val accountId = Random.nextInt()
        val itemId = Random.nextLong()
        val favorite = Random.nextBoolean()
        coEvery { tmdbService.setFavorite(any(), any()) } returns Unit

        val result = remoteDataSource.setFavorite(accountId, itemId, mediaType, favorite)

        Assert.assertEquals(favorite, result)
        coVerify { tmdbService.setFavorite(accountId, FavoriteRequestData(mediaType.type, itemId, favorite)) }
    }

    @Test
    fun `isItemFavorite returns correct type of request`() = runTest {
        val movieId = Random.nextLong()
        val tvShowId = Random.nextLong()
        val movieFavoriteItem: FavoriteItem = randomBuild()
        val tvShowFavoriteItem: FavoriteItem = randomBuild()
        coEvery { tmdbService.isMovieFavorite(any()) } returns movieFavoriteItem
        coEvery { tmdbService.isTvShowFavorite(any()) } returns tvShowFavoriteItem

        val isMovieFavoriteResult = remoteDataSource.isItemFavorite(movieId, MediaType.MOVIE)
        val isTvShowFavoriteResult = remoteDataSource.isItemFavorite(tvShowId, MediaType.TV_SHOW)

        Assert.assertEquals(movieFavoriteItem.favorite, isMovieFavoriteResult)
        Assert.assertEquals(tvShowFavoriteItem.favorite, isTvShowFavoriteResult)
        coVerify { tmdbService.isMovieFavorite(movieId) }
        coVerify { tmdbService.isTvShowFavorite(tvShowId) }
    }
}