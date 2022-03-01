package com.softvision.tmdb.ui.search.model

import com.softvision.tmdb.base.BaseIntent

sealed class SearchIntent : BaseIntent {
    data class QuerySearchIntent(val query: String) : SearchIntent()
    data class GetAdditionalResultsIntent(val page: Int, val query: String) : SearchIntent()
}