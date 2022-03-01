package com.softvision.tmdb.ui.explore

import com.softvision.domain.MovieRepository
import com.softvision.domain.MovieRepository.Companion.INITIAL_PAGE
import com.softvision.model.media.ItemResult
import com.softvision.model.media.SortType
import com.softvision.model.media.TmdbItem
import com.softvision.tmdb.base.BaseViewModel
import com.softvision.tmdb.ui.explore.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class ExploreViewModel @Inject constructor(private val repository: MovieRepository) :
    BaseViewModel<ExploreIntent, ExploreAction, ExploreResult, ExploreState>() {

    override val initialState = ExploreState()

    override fun actionFromIntent(intent: ExploreIntent): ExploreAction =
        when (intent) {
            ExploreIntent.InitialIntent -> ExploreAction.LoadAllCategories
            ExploreIntent.RefreshIntent -> ExploreAction.LoadAllCategories
            is ExploreIntent.GetAdditionalItems -> ExploreAction.LoadCategoryPage(intent.page, intent.sortType)
        }

    override suspend fun resultFromAction(action: ExploreAction): Flow<ExploreResult> =
        when (action) {
            ExploreAction.LoadAllCategories -> flow {
                emit(ExploreResult.LoadAllCategoriesResult.InProgress)
                emit(getCategoriesData())
            }.catch {
                it.printStackTrace()
                emit(ExploreResult.LoadAllCategoriesResult.Failure(it))
            }
            is ExploreAction.LoadCategoryPage -> flow {
                emit(ExploreResult.LoadCategoryResult.InProgress)
                val categoryData = repository.getCategory(action.page, action.sortType).toCategoryData(action.sortType)
                emit(ExploreResult.LoadCategoryResult.Success(categoryData))
            }.catch {
                it.printStackTrace()
                emit(ExploreResult.LoadCategoryResult.Failure(it))
            }
        }

    override fun reduce(previousState: ExploreState, result: ExploreResult): ExploreState =
        when (result) {
            is ExploreResult.LoadAllCategoriesResult.Failure -> previousState.copy(isLoading = false, error = result.error)
            ExploreResult.LoadAllCategoriesResult.InProgress -> previousState.copy(isLoading = true, error = null)
            is ExploreResult.LoadAllCategoriesResult.Success -> previousState.copy(isLoading = false, error = null, categories = result.categories)

            is ExploreResult.LoadCategoryResult.Failure -> previousState.copy(isLoading = false, error = result.error)
            ExploreResult.LoadCategoryResult.InProgress -> previousState.copy(isLoading = true, error = null)
            is ExploreResult.LoadCategoryResult.Success -> previousState.copy(isLoading = false, error = null, categories = mergeCategories(previousState.categories, result.categoryData))
        }

    private fun mergeCategories(categories: List<CategoryData>?, categoryData: CategoryData): List<CategoryData> {
        val returnList = categories ?: listOf()
        val pageExists =
            returnList.any { it.sortType == categoryData.sortType && it.currentPage == categoryData.currentPage }
        if (pageExists)
            return returnList
        val existingCategoryIndex = returnList.indexOfFirst { it.sortType == categoryData.sortType }
        return if (existingCategoryIndex != -1) {
            val temp = returnList.toMutableList()
            val categoryToReplace = temp.removeAt(existingCategoryIndex)
            val updatedCategory = categoryToReplace.copy(
                currentPage = categoryData.currentPage,
                totalPages = categoryData.totalPages,
                items = categoryToReplace.items.plus(categoryData.items).distinct()
            )
            temp.add(existingCategoryIndex, updatedCategory)
            temp
        } else {
            returnList.plus(categoryData)
        }
    }

    private suspend fun getCategoriesData(): ExploreResult {
        val popularItems = repository.getCategory(INITIAL_PAGE, SortType.POPULAR)
        val topRatedItems = repository.getCategory(INITIAL_PAGE, SortType.TOP_RATED)
        val trendingItems = repository.getCategory(INITIAL_PAGE, SortType.TRENDING)
        val upcomingItems = repository.getCategory(INITIAL_PAGE, SortType.UPCOMING)
        val nowPlayingItems = repository.getCategory(INITIAL_PAGE, SortType.NOW_PLAYING)
        val categoryData = listOf(
            popularItems.toCategoryData(SortType.POPULAR),
            topRatedItems.toCategoryData(SortType.TOP_RATED),
            trendingItems.toCategoryData(SortType.TRENDING),
            upcomingItems.toCategoryData(SortType.UPCOMING),
            nowPlayingItems.toCategoryData(SortType.NOW_PLAYING)
        )
        return ExploreResult.LoadAllCategoriesResult.Success(categoryData)
    }


    private fun <T : TmdbItem> ItemResult<T>.toCategoryData(sortType: SortType): CategoryData {
        return CategoryData(page, totalPages, results, sortType)
    }
}
