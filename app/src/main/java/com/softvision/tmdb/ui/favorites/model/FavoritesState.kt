package com.softvision.tmdb.ui.favorites.model

import com.softvision.model.media.TmdbItem
import com.softvision.tmdb.base.BaseState


data class FavoritesState(
    val isLoading: Boolean = false,
    val error: Throwable? = null,
    val tmdbItems: List<TmdbItem>? = null
) : BaseState