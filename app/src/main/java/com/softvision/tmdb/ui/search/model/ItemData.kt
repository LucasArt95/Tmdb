package com.softvision.tmdb.ui.search.model

data class ItemData(
    val itemId: Long,
    val name: String,
    val releaseDate: String?,
    val genres: String,
    val overview: String,
    val backdropPath: String?
)