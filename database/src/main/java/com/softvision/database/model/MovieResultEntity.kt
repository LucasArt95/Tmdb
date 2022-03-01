package com.softvision.database.model

import androidx.room.*
import com.softvision.model.media.SortType

@Entity(
    tableName = "movie_results",
    indices = [Index(value = ["page", "sortType", "genreId"], unique = true)]
)
internal data class MovieResultEntity(
    @PrimaryKey(autoGenerate = true)
    val resultId: Int = 0,
    val page: Int,
    val totalResults: Int,
    val totalPages: Int,
    val sortType: SortType? = null,
    val genreId: Long? = null,
)

@Entity(
    tableName = "movie_result_cross_ref",
    primaryKeys = ["itemId", "resultId"]
)
internal data class MovieResultCrossRef(
    val itemId: Long,
    val resultId: Long,
)

internal data class MovieResultsWithMovies(
    @Embedded val result: MovieResultEntity,
    @Relation(
        parentColumn = "resultId",
        entityColumn = "itemId",
        associateBy = Junction(MovieResultCrossRef::class)
    )
    val movies: List<MovieEntity>,
)