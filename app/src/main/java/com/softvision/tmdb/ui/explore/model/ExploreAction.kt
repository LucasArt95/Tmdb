package com.softvision.tmdb.ui.explore.model

import com.softvision.model.media.SortType
import com.softvision.tmdb.base.BaseAction

sealed class ExploreAction : BaseAction {
    object LoadAllCategories : ExploreAction()
    data class LoadCategoryPage(val page: Int, val sortType: SortType) : ExploreAction()
}