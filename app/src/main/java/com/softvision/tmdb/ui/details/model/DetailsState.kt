package com.softvision.tmdb.ui.details.model

import com.softvision.tmdb.base.BaseState

data class DetailsState(
    val isLoading: Boolean = true,
    val error: Throwable? = null,
    val itemData: ItemData? = null
) : BaseState