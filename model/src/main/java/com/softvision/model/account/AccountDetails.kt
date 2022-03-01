package com.softvision.model.account

import com.google.gson.annotations.SerializedName

data class AccountDetails(
    val avatar: Avatar,
    val id: Int,
    val iso_639_1: String,
    val iso_3166_1: String,
    val name: String,
    @SerializedName("include_adult")
    val includeAdult: Boolean,
    val username: String,
)

data class Avatar(val gravatar: Gravatar, )

data class Gravatar(val hash: String)