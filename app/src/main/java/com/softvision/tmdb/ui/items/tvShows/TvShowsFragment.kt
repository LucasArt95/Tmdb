package com.softvision.tmdb.ui.items.tvShows

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.softvision.model.media.TmdbItem
import com.softvision.model.media.TvShow
import com.softvision.tmdb.ui.items.base.ItemsFragment
import com.softvision.tmdb.ui.items.base.ItemsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TvShowsFragment : ItemsFragment<TvShow>() {

    override val viewModel by viewModels<TvShowsViewModel>()

    override fun onItemClicked(tmdbItem: TmdbItem) {
        val destination = TvShowsFragmentDirections.actionTvShowsToDetails(tmdbItem)
        findNavController().navigate(destination)
    }
}