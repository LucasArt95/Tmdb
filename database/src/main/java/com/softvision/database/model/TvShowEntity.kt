package com.softvision.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tv_shows")
internal data class TvShowEntity(
    @PrimaryKey
    val itemId: Long,
    val name: String,
    val originalName: String,
    val releaseDate: String?,
    val originalLanguage: String,
    val genreIds: List<Long>,
    val popularity: Double,
    val voteCount: Int,
    val voteAverage: Double,
    val overview: String,
    val posterPath: String? = null,
    val backdropPath: String? = null,
    val favorite: Boolean = false,
    val originCountry: List<String>,
)