package com.softvision.tmdb.ui.search.model

import com.softvision.model.media.TmdbItem
import com.softvision.tmdb.base.BaseState

data class SearchState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val tmdbItems: List<TmdbItem>? = null
) : BaseState