package com.softvision.tmdb.details

import android.os.Bundle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.vacxe.konveyor.base.randomBuild
import com.softvision.model.genre.Genre
import com.softvision.model.media.MediaType
import com.softvision.model.media.Movie
import com.softvision.tmdb.R
import com.softvision.tmdb.launchFragmentInHiltContainer
import com.softvision.tmdb.setBagOfTags
import com.softvision.tmdb.ui.details.DetailsFragment
import com.softvision.tmdb.ui.details.DetailsViewModel
import com.softvision.tmdb.ui.details.model.DetailsIntent
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class DetailsFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private val releaseDate = "2022-11-14"
    private val formattedReleaseDate = "14 November 2022"
    private val tmdbItem = randomBuild<Movie>().copy(releaseDate = releaseDate)
    private val genres: List<Genre> = (1..3).map { randomBuild() }

    private val testDispatcher = StandardTestDispatcher()

    @BindValue
    @JvmField
    val detailsViewModel = mockk<DetailsViewModel>() { setBagOfTags() }
//    val detailsViewModel = spyk(DetailsViewModel(mockk()))/* { setBagOfTags() }*/

    @Before
    fun setup() {
        hiltRule.inject()
        Dispatchers.setMain(testDispatcher)
        val arguments = Bundle().apply {
            putParcelable("tmdbItem", tmdbItem)
        }
        launchFragmentInHiltContainer<DetailsFragment>(
            fragmentArgs = arguments,
            themeResId = R.style.Theme_TmdbRemake
        )
    }

    @After
    fun clean() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialIntentIsPassed() {
//        verify { detailsViewModel.submitIntent(DetailsIntent.InitialIntent(tmdbItem)) }
        verify { detailsViewModel.submitIntent(any()) }
    }

    @Test
    fun whenClickOnFavorite_correctIntentIsPassed() {
        onView(withId(R.id.fab_favorite)).perform(click())
        verify {
            detailsViewModel.actionFromIntent(
                DetailsIntent.SetFavoriteIntent(
                    tmdbItem.itemId,
                    MediaType.MOVIE,
                    tmdbItem.favorite
                )
            )
        }
    }

    @Test
    fun showingCorrectData() {
        onView(withId(R.id.tv_title_name)).check(matches(withText(tmdbItem.name)))
        onView(withId(R.id.tv_genres)).check(matches(withText(genres.joinToString { it.name })))
        onView(withId(R.id.tv_release_date)).check(matches(withText(formattedReleaseDate)))
        onView(withId(R.id.tv_description)).check(matches(withText(tmdbItem.overview)))
    }

}