package com.softvision.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.softvision.database.model.GenreEntity
import com.softvision.model.media.MediaType
import org.jetbrains.annotations.TestOnly

@Dao
internal interface GenresDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveGenres(genres: List<GenreEntity>)

    @Query("SELECT * FROM genres WHERE mediaType = :mediaType")
    suspend fun getGenres(mediaType: MediaType): List<GenreEntity>

    @Query("SELECT * FROM genres WHERE mediaType = :mediaType AND id IN (:genreIds)")
    suspend fun getGenres(genreIds: List<Long>, mediaType: MediaType): List<GenreEntity>

    @TestOnly
    @Query("SELECT * FROM genres")
    suspend fun getGenres(): List<GenreEntity>
}