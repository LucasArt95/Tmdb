package com.softvision.tmdb.ui.explore.model

import com.softvision.model.media.SortType
import com.softvision.tmdb.base.BaseIntent

sealed class ExploreIntent : BaseIntent {
    object InitialIntent : ExploreIntent()
    object RefreshIntent : ExploreIntent()
    data class GetAdditionalItems(val page: Int, val sortType: SortType) : ExploreIntent()
}