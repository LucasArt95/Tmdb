package com.softvision.tmdb.ui.items.tvShows

import com.softvision.domain.MovieRepository
import com.softvision.model.genre.Genre
import com.softvision.model.media.ItemResult
import com.softvision.model.media.TvShow
import com.softvision.tmdb.ui.items.base.ItemsViewModel
import com.softvision.tmdb.ui.items.base.model.GenreData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TvShowsViewModel @Inject constructor(private val repository: MovieRepository) : ItemsViewModel<TvShow>() {

    override suspend fun getGenreDataList(): List<GenreData<TvShow>> {
        val genres = repository.getTvShowGenres()
        return genres.map {
            val tvShows = repository.getTvShows(INITIAL_PAGE, it)
            GenreData(tvShows.page, tvShows.totalPages, tvShows.results, it)
        }
    }

    override suspend fun getGenreData(page: Int, genre: Genre): ItemResult<TvShow> {
        return repository.getTvShows(page, genre)
    }
}


