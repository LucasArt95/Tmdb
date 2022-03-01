package com.softvision.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.softvision.model.media.MediaType

@Entity(tableName = "genres")
internal data class GenreEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val mediaType: MediaType
)