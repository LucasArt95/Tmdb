package com.softvision.tmdb.ui.favorites

import com.softvision.domain.MovieRepository
import com.softvision.tmdb.base.BaseViewModel
import com.softvision.tmdb.ui.favorites.model.FavoritesAction
import com.softvision.tmdb.ui.favorites.model.FavoritesIntent
import com.softvision.tmdb.ui.favorites.model.FavoritesResult
import com.softvision.tmdb.ui.favorites.model.FavoritesState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(private val repository: MovieRepository) :
    BaseViewModel<FavoritesIntent, FavoritesAction, FavoritesResult, FavoritesState>() {

    override val initialState: FavoritesState = FavoritesState()

    override fun actionFromIntent(intent: FavoritesIntent): FavoritesAction =
        when (intent) {
            FavoritesIntent.InitialIntent -> FavoritesAction.GetFavorites(INITIAL_PAGE)
            FavoritesIntent.RefreshIntent -> FavoritesAction.GetFavorites(INITIAL_PAGE)
            is FavoritesIntent.GetAdditionalResultsIntent -> FavoritesAction.GetAdditionalResult(intent.page)
        }

    override suspend fun resultFromAction(action: FavoritesAction): Flow<FavoritesResult> =
        when (action) {
            is FavoritesAction.GetFavorites -> flow {
                emit(FavoritesResult.GetFavoritesResult.InProgress)
                emit(FavoritesResult.GetFavoritesResult.Success(repository.getFavoriteItems(action.page)))
            }.catch {
                it.printStackTrace()
                emit(FavoritesResult.GetFavoritesResult.Failure(it))
            }
            is FavoritesAction.GetAdditionalResult -> flow {
                emit(FavoritesResult.GetAdditionalResult.InProgress)
                emit(FavoritesResult.GetAdditionalResult.Success(repository.getFavoriteItems(action.page)))
            }.catch {
                it.printStackTrace()
                emit(FavoritesResult.GetAdditionalResult.Failure(it))
            }
        }

    override fun reduce(previousState: FavoritesState, result: FavoritesResult): FavoritesState =
        when (result) {
            is FavoritesResult.GetFavoritesResult.Failure -> previousState.copy(isLoading = false, error = result.error)
            FavoritesResult.GetFavoritesResult.InProgress -> previousState.copy(isLoading = true, error = null)
            is FavoritesResult.GetFavoritesResult.Success -> previousState.copy(isLoading = false, error = null, tmdbItems = result.itemsResult.results)

            is FavoritesResult.GetAdditionalResult.Failure -> previousState.copy(error = result.error)
            FavoritesResult.GetAdditionalResult.InProgress -> previousState.copy(error = null)
            is FavoritesResult.GetAdditionalResult.Success -> previousState.copy(
                isLoading = false,
                error = null,
                tmdbItems = (previousState.tmdbItems ?: listOf()).plus(result.itemsResult.results).distinct()
            )
        }

    companion object {
        const val INITIAL_PAGE = 1
    }

}

