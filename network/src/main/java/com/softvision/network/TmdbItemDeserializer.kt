package com.softvision.network

import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.softvision.model.media.Movie
import com.softvision.model.media.TmdbItem
import com.softvision.model.media.TvShow
import java.lang.reflect.Type

internal class TmdbItemDeserializer : JsonDeserializer<TmdbItem> {

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): TmdbItem? {
        val tmdbItemType = json?.asJsonObject?.get(MEDIA_TYPE)?.asString
        val gson = Gson()
        return when (tmdbItemType) {
            MOVIE_MEDIA_TYPE -> gson.fromJson(json, Movie::class.java)
            TV_SHOW_MEDIA_TYPE -> gson.fromJson(json, TvShow::class.java)
            else -> null
        }
    }

    companion object {
        const val MEDIA_TYPE = "media_type"
        const val MOVIE_MEDIA_TYPE = "movie"
        const val TV_SHOW_MEDIA_TYPE = "tv"
    }
}