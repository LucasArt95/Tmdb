package com.softvision.tmdb.ui.details.model

import com.softvision.tmdb.base.BaseResult

sealed class DetailsResult : BaseResult {
    sealed class LoadDetailsResult : DetailsResult() {
        data class Success(val itemData: ItemData) : LoadDetailsResult()
        data class Failure(val error: Throwable) : LoadDetailsResult()
        object InProgress : LoadDetailsResult()
    }

    sealed class SetFavoriteResult : DetailsResult() {
        data class Success(val isFavorite: Boolean) : SetFavoriteResult()
        data class Failure(val error: Throwable) : SetFavoriteResult()
        object InProgress : SetFavoriteResult()
    }
}