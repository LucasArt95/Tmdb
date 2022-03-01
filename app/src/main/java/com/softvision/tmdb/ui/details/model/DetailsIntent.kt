package com.softvision.tmdb.ui.details.model

import com.softvision.model.media.MediaType
import com.softvision.model.media.TmdbItem
import com.softvision.tmdb.base.BaseIntent

sealed class DetailsIntent : BaseIntent {
    data class InitialIntent(val tmdbItem: TmdbItem) : DetailsIntent()
    data class SetFavoriteIntent(val itemId: Long, val mediaType: MediaType, val favorite: Boolean) : DetailsIntent()
}