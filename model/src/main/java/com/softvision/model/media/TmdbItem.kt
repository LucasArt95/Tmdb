package com.softvision.model.media

import android.os.Parcelable

interface TmdbItem: Parcelable {
    val itemId: Long
    val name: String
    val originalName: String
    val releaseDate: String?
    val originalLanguage: String
    val genreIds: List<Long>
    val popularity: Double
    val voteCount: Int
    val voteAverage: Double
    val overview: String
    val posterPath: String?
    val backdropPath: String?
    val favorite: Boolean

    override fun equals(other: Any?): Boolean
}
