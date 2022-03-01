package com.softvision.tmdb.ui.items.base.model

import com.softvision.model.genre.Genre
import com.softvision.tmdb.base.BaseIntent

sealed class ItemIntent : BaseIntent {
    object InitialIntent : ItemIntent()
    object RefreshIntent : ItemIntent()
    data class GetAdditionalItems(val page: Int, val genre: Genre) : ItemIntent()
}