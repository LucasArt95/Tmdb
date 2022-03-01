package com.softvision.model.media

import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Movie(
    @SerializedName("id")
    override val itemId: Long,
    @SerializedName("title")
    override val name: String,
    @SerializedName("original_title")
    override val originalName: String,
    @SerializedName("release_date")
    override val releaseDate: String?,
    @SerializedName("original_language")
    override val originalLanguage: String,
    @SerializedName("genre_ids")
    override val genreIds: List<Long>,
    override val popularity: Double,
    @SerializedName("vote_count")
    override val voteCount: Int,
    @SerializedName("vote_average")
    override val voteAverage: Double,
    override val overview: String,
    @SerializedName("poster_path")
    override val posterPath: String? = null,
    @SerializedName("backdrop_path")
    override val backdropPath: String? = null,
    override val favorite: Boolean = false,
    val adult: Boolean,
    val video: Boolean,
) : TmdbItem