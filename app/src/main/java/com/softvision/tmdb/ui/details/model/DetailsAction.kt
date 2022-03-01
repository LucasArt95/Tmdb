package com.softvision.tmdb.ui.details.model

import com.softvision.model.media.MediaType
import com.softvision.model.media.TmdbItem
import com.softvision.tmdb.base.BaseAction

sealed class DetailsAction : BaseAction {
    data class LoadDetails(val tmdbItem: TmdbItem) : DetailsAction()
    data class SetFavorite(val itemId: Long, val mediaType: MediaType, val favorite: Boolean) : DetailsAction()
}