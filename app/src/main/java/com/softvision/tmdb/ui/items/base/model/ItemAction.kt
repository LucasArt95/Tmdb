package com.softvision.tmdb.ui.items.base.model

import com.softvision.model.genre.Genre
import com.softvision.tmdb.base.BaseAction

sealed class ItemAction : BaseAction {
    object LoadAllGenres : ItemAction()
    data class LoadAdditionalPage(val page: Int, val genre: Genre) : ItemAction()
}