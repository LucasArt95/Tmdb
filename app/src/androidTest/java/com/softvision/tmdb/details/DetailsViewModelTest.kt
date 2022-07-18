package com.softvision.tmdb.details
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import com.softvision.tmdb.softvision.data.tmdb.model.Genre
//import com.softvision.tmdb.softvision.data.tmdb.model.MediaType
//import com.softvision.tmdb.softvision.data.tmdb.model.Movie
//import com.softvision.tmdb.softvision.domain.MovieRepository
//import com.softvision.tmdb.softvision.mvi_blueprint.ui.details.model.*
//import com.softvision.tmdb.tyro.oss.arbitrater.arbitrary
//import io.mockk.every
//import io.mockk.mockk
//import io.reactivex.Single
//import org.junit.Assert
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//
//@RunWith(AndroidJUnit4::class)
//class DetailsViewModelTest {
//
//    @get:Rule
//    var instantExecutorRule = InstantTaskExecutorRule()
//
//    private val releaseDate = "2021-11-14"
//    private val formattedReleaseDate = "14 November 2021"
//    private val tmdbItem = arbitrary<Movie>().copy(releaseDate = releaseDate)
//    private val genres: List<Genre> = (1..3).map { arbitrary() }
//    private val itemData = ItemData(tmdbItem.itemId,
//        tmdbItem.name,
//        formattedReleaseDate,
//        genres.joinToString { it.name },
//        tmdbItem.overview,
//        tmdbItem.backdropPath,
//        MediaType.MOVIE,
//        tmdbItem.favorite)
//    private val testException = Exception("Test Exception")
//
//    private val movieRepository = mockk<MovieRepository>()
//    private val detailsViewModel = DetailsViewModel(movieRepository)
//
//    @Before
//    fun setup() {
//        every { movieRepository.getMovieGenres(any()) } returns Single.just(genres)
//        every { movieRepository.getTvShowGenres(any()) } returns Single.just(genres)
//        every { movieRepository.setFavorite(any(), any(), any()) } returns Single.just(true)
//    }
//
//    @Test
//    fun actionFromIntentReturnsCorrectAction() {
//        var result = detailsViewModel.actionFromIntent(DetailsIntent.InitialIntent(tmdbItem))
//        Assert.assertEquals(DetailsAction.LoadDetails(tmdbItem), result)
//        result = detailsViewModel.actionFromIntent(DetailsIntent.SetFavoriteIntent(tmdbItem.itemId,
//            MediaType.MOVIE,
//            tmdbItem.favorite))
//        Assert.assertEquals(DetailsAction.SetFavorite(tmdbItem.itemId,
//            MediaType.MOVIE,
//            tmdbItem.favorite), result)
//    }
//
//    @Test
//    fun resultFromActionLoadDetailsReturnsCorrectResult() {
//        every { movieRepository.isItemFavorite(any(), any()) } returns Single.just(false)
//        val observable = detailsViewModel.resultFromAction(DetailsAction.LoadDetails(tmdbItem))
//        var result = observable.elementAt(0).blockingGet()
//        Assert.assertEquals(DetailsResult.LoadDetailsResult.InProgress, result)
//
//        result = observable.elementAt(1).blockingGet()
//        val expected = DetailsResult.LoadDetailsResult.Success(itemData)
//        Assert.assertEquals(expected, result)
//    }
//
//    @Test
//    fun resultFromActionLoadDetailsReturnsCorrectError() {
//        every { movieRepository.isItemFavorite(any(), any()) } returns Single.error(testException)
//        val observable = detailsViewModel.resultFromAction(DetailsAction.LoadDetails(tmdbItem))
//        var result = observable.elementAt(0).blockingGet()
//        Assert.assertEquals(DetailsResult.LoadDetailsResult.InProgress, result)
//
//        result = observable.elementAt(1).blockingGet()
//        Assert.assertEquals(DetailsResult.LoadDetailsResult.Failure(testException), result)
//    }
//
//    @Test
//    fun resultFromActionSetFavoriteReturnsCorrectResult() {
//        every { movieRepository.setFavorite(any(), any(), any()) } returns Single.just(true)
//        val observable =
//            detailsViewModel.resultFromAction(DetailsAction.SetFavorite(tmdbItem.itemId,
//                MediaType.MOVIE,
//                tmdbItem.favorite))
//        var result = observable.elementAt(0).blockingGet()
//        Assert.assertEquals(DetailsResult.SetFavoriteResult.InProgress, result)
//
//        result = observable.elementAt(1).blockingGet()
//        val expected = DetailsResult.SetFavoriteResult.Success(true)
//        Assert.assertEquals(expected, result)
//    }
//
//    @Test
//    fun resultFromActionSetFavoriteReturnsCorrectError() {
//        every { movieRepository.setFavorite(any(), any(), any()) } returns
//                Single.error(testException)
//        val observable =
//            detailsViewModel.resultFromAction(DetailsAction.SetFavorite(tmdbItem.itemId,
//                MediaType.MOVIE,
//                tmdbItem.favorite))
//        var result = observable.elementAt(0).blockingGet()
//        Assert.assertEquals(DetailsResult.SetFavoriteResult.InProgress, result)
//
//        result = observable.elementAt(1).blockingGet()
//        Assert.assertEquals(DetailsResult.SetFavoriteResult.Failure(testException), result)
//    }
//
//    @Test
//    fun reduceReturnsCorrectState() {
//        val initialState = DetailsState()
//        var result =
//            detailsViewModel.reduce(initialState, DetailsResult.LoadDetailsResult.InProgress)
//        Assert.assertEquals(initialState.copy(isLoading = true, error = null), result)
//
//        result =
//            detailsViewModel.reduce(initialState, DetailsResult.LoadDetailsResult.Success(itemData))
//        Assert.assertEquals(initialState.copy(isLoading = false, itemData = itemData), result)
//
//        result = detailsViewModel.reduce(initialState,
//            DetailsResult.LoadDetailsResult.Failure(testException))
//        Assert.assertEquals(initialState.copy(isLoading = false, error = testException), result)
//
//        result = detailsViewModel.reduce(initialState, DetailsResult.SetFavoriteResult.InProgress)
//        Assert.assertEquals(initialState, result)
//
//        result = detailsViewModel.reduce(initialState,
//            DetailsResult.SetFavoriteResult.Success(!tmdbItem.favorite))
//        val expected = initialState.copy(itemData = initialState.itemData?.copy(isFavorite = true))
//        Assert.assertEquals(expected, result)
//
//        result = detailsViewModel.reduce(initialState,
//            DetailsResult.SetFavoriteResult.Failure(testException))
//        Assert.assertEquals(initialState.copy(error = testException), result)
//    }
//
//
//}