package com.softvision.tmdb.ui.items.base.model

import com.softvision.model.media.TmdbItem
import com.softvision.tmdb.base.BaseResult

sealed class ItemsResult : BaseResult {
    sealed class LoadAllGenresResult : ItemsResult() {
        data class Success<T : TmdbItem>(val genres: List<GenreData<T>>) : LoadAllGenresResult()
        data class Failure(val error: Throwable) : LoadAllGenresResult()
        object InProgress : LoadAllGenresResult()
    }

    sealed class LoadAdditionalPageResult : ItemsResult() {
        data class Success<T : TmdbItem>(val genre: GenreData<T>) : LoadAdditionalPageResult()
        data class Failure(val error: Throwable) : LoadAdditionalPageResult()
        object InProgress : LoadAdditionalPageResult()
    }

}