package com.softvision.tmdb.ui.items.base.model

import com.softvision.model.genre.Genre
import com.softvision.model.media.TmdbItem

data class GenreData<T : TmdbItem>(
    val currentPage: Int,
    val totalPages: Int,
    val items: List<T>,
    val genre: Genre
)