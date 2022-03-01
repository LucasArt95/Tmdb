package com.softvision.tmdb.ui.details

import com.softvision.domain.MovieRepository
import com.softvision.model.media.MediaType
import com.softvision.model.media.Movie
import com.softvision.model.media.TmdbItem
import com.softvision.tmdb.base.BaseViewModel
import com.softvision.tmdb.ui.details.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val repository: MovieRepository) :
    BaseViewModel<DetailsIntent, DetailsAction, DetailsResult, DetailsState>() {

    override val initialState: DetailsState = DetailsState()

    override fun actionFromIntent(intent: DetailsIntent): DetailsAction =
        when (intent) {
            is DetailsIntent.InitialIntent -> DetailsAction.LoadDetails(intent.tmdbItem)
            is DetailsIntent.SetFavoriteIntent -> DetailsAction.SetFavorite(intent.itemId, intent.mediaType, intent.favorite)
        }

    override suspend fun resultFromAction(action: DetailsAction): Flow<DetailsResult> =
        when (action) {
            is DetailsAction.LoadDetails -> flow {
                emit(DetailsResult.LoadDetailsResult.InProgress)
                emit(DetailsResult.LoadDetailsResult.Success(getItemData(action.tmdbItem)))
            }.catch {
                it.printStackTrace()
                emit(DetailsResult.LoadDetailsResult.Failure(it))
            }
            is DetailsAction.SetFavorite -> flow {
                emit(DetailsResult.SetFavoriteResult.InProgress)
                emit(DetailsResult.SetFavoriteResult.Success(repository.setFavorite(action.itemId, action.mediaType, !action.favorite)))
            }.catch {
                it.printStackTrace()
                emit(DetailsResult.SetFavoriteResult.Failure(it))
            }
        }

    override fun reduce(previousState: DetailsState, result: DetailsResult): DetailsState =
        when (result) {
            DetailsResult.LoadDetailsResult.InProgress -> previousState.copy(isLoading = true, error = null)
            is DetailsResult.LoadDetailsResult.Success -> previousState.copy(isLoading = false, error = null, itemData = result.itemData)
            is DetailsResult.LoadDetailsResult.Failure -> previousState.copy(isLoading = false, error = result.error)

            is DetailsResult.SetFavoriteResult.Failure -> previousState.copy(error = result.error)
            DetailsResult.SetFavoriteResult.InProgress -> previousState
            is DetailsResult.SetFavoriteResult.Success -> previousState.copy(
                itemData = previousState.itemData?.copy(isFavorite = result.isFavorite)
            )
        }

    private suspend fun getGenres(tmdbItem: TmdbItem) =
        (if (tmdbItem is Movie)
            repository.getMovieGenres(tmdbItem.genreIds)
        else repository.getTvShowGenres(tmdbItem.genreIds))
            .joinToString { it.name }


    private suspend fun isItemFavorite(tmdbItem: TmdbItem): Boolean {
        val mediaType = MediaType.fromValue(tmdbItem)
        return repository.isItemFavorite(tmdbItem.itemId, mediaType)
    }

    private suspend fun getItemData(tmdbItem: TmdbItem): ItemData {
        val genres = getGenres(tmdbItem)
        val isFavorite = isItemFavorite(tmdbItem)
        return tmdbItem.toItemData(genres, isFavorite)
    }


    private fun TmdbItem.toItemData(genres: String, isFavorite: Boolean): ItemData {
        val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val formatter = SimpleDateFormat("dd MMMM yyyy", Locale.US)
        val releaseDateString = try {
            val parsed = parser.parse(releaseDate!!)
            formatter.format(parsed)
        } catch (e: Exception) {
            null
        }
        val mediaType = MediaType.fromValue(this)
        return ItemData(
            itemId,
            name,
            releaseDateString,
            genres,
            overview,
            backdropPath,
            mediaType,
            isFavorite
        )
    }
}

