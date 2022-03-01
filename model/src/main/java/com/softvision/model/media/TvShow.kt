package com.softvision.model.media

import com.google.gson.annotations.SerializedName
import com.softvision.model.media.TmdbItem
import kotlinx.parcelize.Parcelize

@Parcelize
data class TvShow(
    @SerializedName("id")
    override val itemId: Long,
    override val name: String,
    @SerializedName("original_name")
    override val originalName: String,
    @SerializedName("first_air_date")
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
    @SerializedName("origin_country")
    val originCountry: List<String>,
) : TmdbItem
