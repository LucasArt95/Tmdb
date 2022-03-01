package com.softvision.tmdb.ui.items.base.model

import com.softvision.model.media.TmdbItem
import com.softvision.tmdb.base.BaseState

data class ItemState<T : TmdbItem>(
    val isLoading: Boolean = true,
    val error: Throwable? = null,
    val genres: List<GenreData<T>>? = null
) : BaseState