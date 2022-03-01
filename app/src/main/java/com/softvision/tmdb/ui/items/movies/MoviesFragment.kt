package com.softvision.tmdb.ui.items.movies

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.softvision.model.media.Movie
import com.softvision.model.media.TmdbItem
import com.softvision.tmdb.ui.items.base.ItemsFragment
import com.softvision.tmdb.ui.items.base.ItemsViewModel
import com.softvision.tmdb.ui.items.base.model.ItemIntent
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoviesFragment : ItemsFragment<Movie>() {

    override val viewModel by viewModels<MoviesViewModel>()

    override fun onItemClicked(tmdbItem: TmdbItem) {
        val destination = MoviesFragmentDirections.actionMoviesToDetails(tmdbItem)
        findNavController().navigate(destination)
    }

}