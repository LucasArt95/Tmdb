package com.softvision.network.model

import com.google.gson.annotations.SerializedName

internal data class FavoriteItem(
    @SerializedName("id")
    val itemId: String,
    val favorite: Boolean
)