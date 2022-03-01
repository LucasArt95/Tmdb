package com.softvision.tmdb.ui.search.model

import com.softvision.tmdb.base.BaseAction

sealed class SearchAction : BaseAction {
    data class QuerySearchAction(val page: Int, val query: String) : SearchAction()
    data class GetAdditionalResultsAction(val page: Int, val query: String) : SearchAction()
}