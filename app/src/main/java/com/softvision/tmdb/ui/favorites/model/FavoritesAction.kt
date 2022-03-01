package com.softvision.tmdb.ui.favorites.model

import com.softvision.tmdb.base.BaseAction


sealed class FavoritesAction : BaseAction {
    data class GetFavorites(val page: Int) : FavoritesAction()
    data class GetAdditionalResult(val page: Int) : FavoritesAction()
}