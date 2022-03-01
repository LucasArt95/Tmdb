package com.softvision.tmdb.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.softvision.tmdb.base.BaseFragment
import com.softvision.tmdb.databinding.FragmentFavoritesBinding
import com.softvision.tmdb.ui.base.CategoryAdapter
import com.softvision.tmdb.ui.favorites.model.FavoritesAction
import com.softvision.tmdb.ui.favorites.model.FavoritesIntent
import com.softvision.tmdb.ui.favorites.model.FavoritesResult
import com.softvision.tmdb.ui.favorites.model.FavoritesState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : BaseFragment<FavoritesIntent, FavoritesAction, FavoritesResult, FavoritesState>() {

    override val viewModel by viewModels<FavoritesViewModel>()
    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var categoryAdapter: CategoryAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null)
            viewModel.submitIntent(FavoritesIntent.InitialIntent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun initView() {
        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.submitIntent(FavoritesIntent.RefreshIntent) }
        initRecyclerView()
    }

    override fun render(state: FavoritesState) {
        binding.swipeRefreshLayout.isRefreshing = state.isLoading
        if (state.error != null) showErrorToast(state.error)
        binding.viewData = state
        categoryAdapter.submitList(state.tmdbItems)
    }

    private fun initRecyclerView() {
        val layoutManager = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
        categoryAdapter = CategoryAdapter { _, item ->
            val destination = FavoritesFragmentDirections.actionFavoritesToDetails(item)
            findNavController().navigate(destination)
        }
        categoryAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.rvSearchItems.adapter = categoryAdapter
        binding.rvSearchItems.layoutManager = layoutManager

    }
}