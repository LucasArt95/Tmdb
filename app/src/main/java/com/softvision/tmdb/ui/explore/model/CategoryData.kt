package com.softvision.tmdb.ui.explore.model

import com.softvision.model.media.SortType
import com.softvision.model.media.TmdbItem


data class CategoryData(
    val currentPage: Int,
    val totalPages: Int,
    val items: List<TmdbItem>,
    val sortType: SortType
)