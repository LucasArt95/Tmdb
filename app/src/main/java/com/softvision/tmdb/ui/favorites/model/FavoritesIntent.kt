package com.softvision.tmdb.ui.favorites.model

import com.softvision.tmdb.base.BaseIntent

sealed class FavoritesIntent : BaseIntent {
    object InitialIntent : FavoritesIntent()
    object RefreshIntent : FavoritesIntent()
    data class GetAdditionalResultsIntent(val page: Int) : FavoritesIntent()
}