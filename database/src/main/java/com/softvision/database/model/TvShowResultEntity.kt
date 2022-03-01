package com.softvision.database.model

import androidx.room.*
import com.softvision.model.media.SortType

@Entity(
    tableName = "tv_show_results",
    indices = [Index(value = ["page", "sortType", "genreId"], unique = true)]
)
internal data class TvShowResultEntity(
    @PrimaryKey(autoGenerate = true)
    val resultId: Int = 0,
    val page: Int,
    val totalResults: Int,
    val totalPages: Int,
    var sortType: SortType? = null,
    val genreId: Long? = null,
)

@Entity(tableName = "tv_show_result_cross_ref", primaryKeys = ["itemId", "resultId"])
internal data class TvShowResultCrossRef(
    val itemId: Long,
    val resultId: Long,
)

internal data class TvShowResultsWithTvShows(
    @Embedded val result: TvShowResultEntity,
    @Relation(
        parentColumn = "resultId",
        entityColumn = "itemId",
        associateBy = Junction(TvShowResultCrossRef::class)
    )
    val tvShows: List<TvShowEntity>,
)