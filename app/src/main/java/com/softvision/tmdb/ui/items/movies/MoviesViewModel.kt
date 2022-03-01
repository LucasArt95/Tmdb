package com.softvision.tmdb.ui.items.movies

import com.softvision.domain.MovieRepository
import com.softvision.model.genre.Genre
import com.softvision.model.media.ItemResult
import com.softvision.model.media.Movie
import com.softvision.tmdb.ui.items.base.ItemsViewModel
import com.softvision.tmdb.ui.items.base.model.GenreData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(private val repository: MovieRepository) : ItemsViewModel<Movie>() {

    override suspend fun getGenreDataList(): List<GenreData<Movie>> {
        val genres = repository.getMovieGenres()
        return genres.map {
            val movies = repository.getMovies(INITIAL_PAGE, it)
            GenreData(movies.page, movies.totalPages, movies.results, it)
        }
    }

    override suspend fun getGenreData(page: Int, genre: Genre): ItemResult<Movie> {
        return repository.getMovies(page, genre)
    }

}


