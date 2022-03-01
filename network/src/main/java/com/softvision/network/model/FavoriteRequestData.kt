package com.softvision.network.model

import com.google.gson.annotations.SerializedName

internal data class FavoriteRequestData(
    @SerializedName("media_type")
    val mediaType: String,
    @SerializedName("media_id")
    val mediaId: Long,
    val favorite: Boolean,
)

