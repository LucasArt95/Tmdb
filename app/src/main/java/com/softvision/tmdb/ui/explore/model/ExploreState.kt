package com.softvision.tmdb.ui.explore.model

import com.softvision.tmdb.base.BaseState

data class ExploreState(
    val isLoading: Boolean = true,
    val error: Throwable? = null,
    val categories: List<CategoryData>? = null
) : BaseState