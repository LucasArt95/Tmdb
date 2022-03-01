package com.softvision.tmdb.ui.explore.model

import com.softvision.tmdb.base.BaseResult

sealed class ExploreResult : BaseResult {

    sealed class LoadAllCategoriesResult : ExploreResult() {
        data class Success(val categories: List<CategoryData>) : LoadAllCategoriesResult()
        data class Failure(val error: Throwable) : LoadAllCategoriesResult()
        object InProgress : LoadAllCategoriesResult()
    }

    sealed class LoadCategoryResult : ExploreResult() {
        data class Success(val categoryData: CategoryData) : LoadCategoryResult()
        data class Failure(val error: Throwable) : LoadCategoryResult()
        object InProgress : LoadCategoryResult()
    }
}