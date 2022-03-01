package com.softvision.tmdb.ui.search

import android.os.Bundle
import android.text.InputType
import android.view.*
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.softvision.tmdb.R
import com.softvision.tmdb.base.BaseFragment
import com.softvision.tmdb.databinding.FragmentSearchBinding
import com.softvision.tmdb.ui.base.CategoryAdapter
import com.softvision.tmdb.ui.search.model.SearchAction
import com.softvision.tmdb.ui.search.model.SearchIntent
import com.softvision.tmdb.ui.search.model.SearchResult
import com.softvision.tmdb.ui.search.model.SearchState
import com.softvision.tmdb.utils.ViewExtensions.queryFlow
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SearchFragment : BaseFragment<SearchIntent, SearchAction, SearchResult, SearchState>() {

    override val viewModel by viewModels<SearchViewModel>()
    private lateinit var binding: FragmentSearchBinding
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_hint)
        searchView.imeOptions = EditorInfo.IME_ACTION_SEARCH
        searchView.inputType = InputType.TYPE_CLASS_TEXT
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                searchView.queryFlow()
                    .debounce(TIMEOUT_SEARCH_MILLIS)
                    .collect { it?.let { viewModel.submitIntent(SearchIntent.QuerySearchIntent(it)) } }
            }
        }
    }

    override fun initView() {
        initRecyclerView()
    }

    override fun render(state: SearchState) {
        binding.flLoading.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        if (state.error != null) showErrorToast(state.error)
        binding.viewData = state
        categoryAdapter.submitList(state.tmdbItems)
    }

    private fun initRecyclerView() {
        val layoutManager = GridLayoutManager(context, 3, RecyclerView.VERTICAL, false)
        categoryAdapter = CategoryAdapter { _, item ->
            val destination = SearchFragmentDirections.actionSearchToDetails(item)
            findNavController().navigate(destination)
        }
        categoryAdapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        binding.rvSearchItems.adapter = categoryAdapter
        binding.rvSearchItems.layoutManager = layoutManager

    }

    companion object {
        const val TIMEOUT_SEARCH_MILLIS = 1000L
    }

}