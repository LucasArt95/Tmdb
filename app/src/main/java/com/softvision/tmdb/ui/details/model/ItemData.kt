package com.softvision.tmdb.ui.details.model

import com.softvision.model.media.MediaType


data class ItemData(
    val itemId: Long,
    val name: String,
    val releaseDate: String?,
    val genres: String,
    val overview: String,
    val backdropPath: String?,
    val mediaType: MediaType,
    val isFavorite: Boolean = false,
)