package com.softvision.model.media

enum class MediaType(val type: String) {
    MOVIE("movie"), TV_SHOW("tv");

    companion object {
        fun fromValue(tmdbItem: TmdbItem): MediaType {
            return if (tmdbItem is Movie) MOVIE
            else TV_SHOW
        }
    }
}