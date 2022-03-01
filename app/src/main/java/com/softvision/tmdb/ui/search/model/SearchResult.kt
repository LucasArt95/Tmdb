package com.softvision.tmdb.ui.search.model

import com.softvision.model.media.ItemResult
import com.softvision.model.media.TmdbItem
import com.softvision.tmdb.base.BaseResult

sealed class SearchResult : BaseResult {
    sealed class QuerySearchResult : SearchResult() {
        data class Success(val itemsResult: ItemResult<TmdbItem>) : QuerySearchResult()
        data class Failure(val error: Throwable) : QuerySearchResult()
        object InProgress : QuerySearchResult()
    }

    sealed class GetAdditionalResults : SearchResult() {
        data class Success(val itemsResult: ItemResult<TmdbItem>) : GetAdditionalResults()
        data class Failure(val error: Throwable) : GetAdditionalResults()
        object InProgress : GetAdditionalResults()
    }
}