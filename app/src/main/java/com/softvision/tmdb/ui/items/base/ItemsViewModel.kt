package com.softvision.tmdb.ui.items.base

import com.softvision.model.genre.Genre
import com.softvision.model.media.ItemResult
import com.softvision.model.media.TmdbItem
import com.softvision.tmdb.base.BaseViewModel
import com.softvision.tmdb.ui.items.base.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

abstract class ItemsViewModel<T : TmdbItem> :
    BaseViewModel<ItemIntent, ItemAction, ItemsResult, ItemState<T>>() {

    override val initialState: ItemState<T> = ItemState()

    override fun actionFromIntent(intent: ItemIntent): ItemAction = when (intent) {
        ItemIntent.InitialIntent -> ItemAction.LoadAllGenres
        ItemIntent.RefreshIntent -> ItemAction.LoadAllGenres
        is ItemIntent.GetAdditionalItems -> ItemAction.LoadAdditionalPage(intent.page, intent.genre)
    }


    override suspend fun resultFromAction(action: ItemAction): Flow<ItemsResult> =
        when (action) {
            ItemAction.LoadAllGenres -> flow {
                emit(ItemsResult.LoadAllGenresResult.InProgress)
                emit(ItemsResult.LoadAllGenresResult.Success(getGenreDataList()))
            }.catch {
                it.printStackTrace()
                emit(ItemsResult.LoadAllGenresResult.Failure(it))
            }
            is ItemAction.LoadAdditionalPage -> flow {
                emit(ItemsResult.LoadAdditionalPageResult.InProgress)
                emit(ItemsResult.LoadAdditionalPageResult.Success(getGenreData(action.page, action.genre).toGenreData(action.genre)))
            }.catch {
                it.printStackTrace()
                emit(ItemsResult.LoadAdditionalPageResult.Failure(it))
            }
        }

    @Suppress("UNCHECKED_CAST")
    override fun reduce(previousState: ItemState<T>, result: ItemsResult): ItemState<T> =
        when (result) {
            is ItemsResult.LoadAdditionalPageResult.Failure -> previousState.copy(error = result.error)
            ItemsResult.LoadAdditionalPageResult.InProgress -> previousState.copy()
            is ItemsResult.LoadAdditionalPageResult.Success<*> -> previousState.copy(
                genres = previousState.genres.merge(result.genre as GenreData<T>).distinct()
            )
            is ItemsResult.LoadAllGenresResult.Failure -> previousState.copy(isLoading = false, error = result.error)
            ItemsResult.LoadAllGenresResult.InProgress -> previousState.copy(isLoading = true, error = null)
            is ItemsResult.LoadAllGenresResult.Success<*> -> previousState.copy(
                isLoading = false,
                error = null,
                genres = result.genres as List<GenreData<T>>
            )
        }

    abstract suspend fun getGenreDataList(): List<GenreData<T>>

    abstract suspend fun getGenreData(page: Int, genre: Genre): ItemResult<T>

    private fun ItemResult<T>.toGenreData(genre: Genre): GenreData<T> {
        return GenreData(page, totalPages, results, genre)
    }

    private fun List<GenreData<T>>?.merge(genreData: GenreData<T>): List<GenreData<T>> {
        val returnList = this ?: listOf()
        val pageExists =
            returnList.any { it.genre.id == genreData.genre.id && it.currentPage == genreData.currentPage }
        if (pageExists)
            return returnList
        val existingCategoryIndex = returnList.indexOfFirst { it.genre == genreData.genre }
        return if (existingCategoryIndex != -1) {
            val temp = returnList.toMutableList()
            val categoryToReplace = temp.removeAt(existingCategoryIndex)
            val updatedCategory = categoryToReplace.copy(
                currentPage = genreData.currentPage,
                totalPages = genreData.totalPages,
                items = categoryToReplace.items.plus(genreData.items)
            )
            temp.add(existingCategoryIndex, updatedCategory)
            temp
        } else {
            returnList.plus(genreData)
        }

    }

    companion object {
        const val INITIAL_PAGE = 1
    }
}



