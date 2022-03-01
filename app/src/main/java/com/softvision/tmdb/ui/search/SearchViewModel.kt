package com.softvision.tmdb.ui.search

import com.softvision.domain.MovieRepository
import com.softvision.tmdb.base.BaseViewModel
import com.softvision.tmdb.ui.search.model.SearchAction
import com.softvision.tmdb.ui.search.model.SearchIntent
import com.softvision.tmdb.ui.search.model.SearchResult
import com.softvision.tmdb.ui.search.model.SearchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(private val repository: MovieRepository) :
    BaseViewModel<SearchIntent, SearchAction, SearchResult, SearchState>() {

    override val initialState: SearchState = SearchState()

    override fun actionFromIntent(intent: SearchIntent): SearchAction =
        when (intent) {
            is SearchIntent.QuerySearchIntent -> SearchAction.QuerySearchAction(INITIAL_PAGE, intent.query)
            is SearchIntent.GetAdditionalResultsIntent -> SearchAction.GetAdditionalResultsAction(intent.page, intent.query)
        }

    override suspend fun resultFromAction(action: SearchAction): Flow<SearchResult> =
        when (action) {
            is SearchAction.QuerySearchAction -> flow {
                emit(SearchResult.QuerySearchResult.InProgress)
                emit(SearchResult.QuerySearchResult.Success(repository.search(action.page, action.query)))
            }.catch {
                it.printStackTrace()
                emit(SearchResult.QuerySearchResult.Failure(it))
            }
            is SearchAction.GetAdditionalResultsAction -> flow {
                emit(SearchResult.GetAdditionalResults.InProgress)
                emit(SearchResult.GetAdditionalResults.Success(repository.search(action.page, action.query)))
            }.catch {
                it.printStackTrace()
                emit(SearchResult.GetAdditionalResults.Failure(it))
            }
        }


    override fun reduce(previousState: SearchState, result: SearchResult): SearchState =
        when (result) {
            SearchResult.QuerySearchResult.InProgress -> previousState.copy(isLoading = true, error = null)
            is SearchResult.QuerySearchResult.Failure -> previousState.copy(isLoading = false, error = result.error)
            is SearchResult.QuerySearchResult.Success -> previousState.copy(isLoading = false, error = null, tmdbItems = result.itemsResult.results)

            SearchResult.GetAdditionalResults.InProgress -> previousState.copy(isLoading = false, error = null)
            is SearchResult.GetAdditionalResults.Failure -> previousState.copy(isLoading = false, error = result.error)
            is SearchResult.GetAdditionalResults.Success -> previousState.copy(
                isLoading = false,
                error = null,
                tmdbItems = (previousState.tmdbItems ?: listOf()).plus(result.itemsResult.results).distinct()
            )
        }

    companion object {
        const val INITIAL_PAGE = 1
    }

}

