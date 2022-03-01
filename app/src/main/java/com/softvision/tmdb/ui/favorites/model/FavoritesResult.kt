package com.softvision.tmdb.ui.favorites.model

import com.softvision.model.media.ItemResult
import com.softvision.model.media.TmdbItem
import com.softvision.tmdb.base.BaseResult

sealed class FavoritesResult : BaseResult {
    sealed class GetFavoritesResult : FavoritesResult() {
        data class Success(val itemsResult: ItemResult<TmdbItem>) : GetFavoritesResult()
        data class Failure(val error: Throwable) : GetFavoritesResult()
        object InProgress : GetFavoritesResult()
    }

    sealed class GetAdditionalResult : FavoritesResult() {
        data class Success(val itemsResult: ItemResult<TmdbItem>) : GetAdditionalResult()
        data class Failure(val error: Throwable) : GetAdditionalResult()
        object InProgress : GetAdditionalResult()
    }
}