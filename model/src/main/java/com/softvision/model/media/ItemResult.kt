package com.softvision.model.media

import com.google.gson.annotations.SerializedName

data class ItemResult<T : TmdbItem>(
    val page: Int,
    val results: List<T>,
    @SerializedName("total_results")
    val totalResults: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
)
