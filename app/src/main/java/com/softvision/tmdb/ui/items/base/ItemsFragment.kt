package com.softvision.tmdb.ui.items.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softvision.model.media.TmdbItem
import com.softvision.tmdb.base.BaseFragment
import com.softvision.tmdb.databinding.FragmentTmbdItemsBinding
import com.softvision.tmdb.ui.items.base.model.*


abstract class ItemsFragment<T : TmdbItem> :
    BaseFragment<ItemIntent, ItemAction, ItemsResult, ItemState<T>>(),
    GenreAdapter.GenreAdapterListener {

    abstract override val viewModel: ItemsViewModel<T>
    private lateinit var binding: FragmentTmbdItemsBinding
    private lateinit var adapter: GenreAdapter<T>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null)
            viewModel.submitIntent(ItemIntent.InitialIntent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentTmbdItemsBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun initView() {
        adapter = GenreAdapter()
        adapter.setGenreAdapterListener(this)
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.rvGenreItems.adapter = adapter
        binding.swipeRefreshLayout.setOnRefreshListener { viewModel.submitIntent(ItemIntent.RefreshIntent) }
    }

    override fun render(state: ItemState<T>) {
        showLoadingBar(state.isLoading)
        if (state.error != null) showErrorToast(state.error)
        adapter.submitList(state.genres)
    }

    private fun showLoadingBar(show: Boolean) {
        binding.swipeRefreshLayout.isRefreshing = show
    }

    abstract override fun onItemClicked(tmdbItem: TmdbItem)

}

