package com.softvision.tmdb.ui.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.transition.MaterialElevationScale
import com.softvision.model.media.SortType
import com.softvision.model.media.TmdbItem
import com.softvision.tmdb.R
import com.softvision.tmdb.base.BaseFragment
import com.softvision.tmdb.databinding.FragmentExploreBinding
import com.softvision.tmdb.ui.base.CategoryAdapter
import com.softvision.tmdb.ui.explore.model.ExploreAction
import com.softvision.tmdb.ui.explore.model.ExploreIntent
import com.softvision.tmdb.ui.explore.model.ExploreResult
import com.softvision.tmdb.ui.explore.model.ExploreState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreFragment : BaseFragment<ExploreIntent, ExploreAction, ExploreResult, ExploreState>() {

    override val viewModel by viewModels<ExploreViewModel>()
    private lateinit var binding: FragmentExploreBinding

    private lateinit var popularCategoryAdapter: CategoryAdapter
    private lateinit var topRatedCategoryAdapter: CategoryAdapter
    private lateinit var trendingCategoryAdapter: CategoryAdapter
    private lateinit var upcomingCategoryAdapter: CategoryAdapter
    private lateinit var nowPlayingCategoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null)
            viewModel.submitIntent(ExploreIntent.InitialIntent)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun initView() {
        initRecyclerViews()
        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.submitIntent(ExploreIntent.RefreshIntent) }
    }

    override fun render(state: ExploreState) {
        showLoadingBar(state.isLoading)
        if (state.error != null) showErrorToast(state.error)
        popularCategoryAdapter.submitList(state.categories?.firstOrNull { it.sortType == SortType.POPULAR }?.items)
        topRatedCategoryAdapter.submitList(state.categories?.firstOrNull { it.sortType == SortType.TOP_RATED }?.items)
        trendingCategoryAdapter.submitList(state.categories?.firstOrNull { it.sortType == SortType.TRENDING }?.items)
        upcomingCategoryAdapter.submitList(state.categories?.firstOrNull { it.sortType == SortType.UPCOMING }?.items)
        nowPlayingCategoryAdapter.submitList(state.categories?.firstOrNull { it.sortType == SortType.NOW_PLAYING }?.items)
    }

    private fun showLoadingBar(show: Boolean) {
        binding.swipeRefreshLayout.isRefreshing = show
    }

    private fun initRecyclerViews() {
        val onItemClicked: (View, TmdbItem) -> Unit = { _, item ->
            val destination = ExploreFragmentDirections.actionExploreToDetails(item)
            findNavController().navigate(destination)
        }

        popularCategoryAdapter = CategoryAdapter(onItemClicked)
        topRatedCategoryAdapter = CategoryAdapter(onItemClicked)
        trendingCategoryAdapter = CategoryAdapter(onItemClicked)
        upcomingCategoryAdapter = CategoryAdapter(onItemClicked)
        nowPlayingCategoryAdapter = CategoryAdapter(onItemClicked)
        initRecyclerView(popularCategoryAdapter, SortType.POPULAR)
        initRecyclerView(topRatedCategoryAdapter, SortType.TOP_RATED)
        initRecyclerView(trendingCategoryAdapter, SortType.TRENDING)
        initRecyclerView(upcomingCategoryAdapter, SortType.UPCOMING)
        initRecyclerView(nowPlayingCategoryAdapter, SortType.NOW_PLAYING)

    }

    private fun initRecyclerView(adapter: CategoryAdapter, sortType: SortType) {
        val layoutManagerProducer = {
            LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        }
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        when (sortType) {
            SortType.POPULAR -> {
                binding.rvPopularItems.adapter = adapter
                binding.rvPopularItems.layoutManager = layoutManagerProducer()
            }
            SortType.TOP_RATED -> {
                binding.rvTopRatedItems.adapter = adapter
                binding.rvTopRatedItems.layoutManager = layoutManagerProducer()
            }
            SortType.TRENDING -> {
                binding.rvTrendingItems.adapter = adapter
                binding.rvTrendingItems.layoutManager = layoutManagerProducer()
            }
            SortType.UPCOMING -> {
                binding.rvUpcomingItems.adapter = adapter
                binding.rvUpcomingItems.layoutManager = layoutManagerProducer()
            }
            SortType.NOW_PLAYING -> {
                binding.rvNowPlayingItems.adapter = adapter
                binding.rvNowPlayingItems.layoutManager = layoutManagerProducer()
            }
        }
    }
}

