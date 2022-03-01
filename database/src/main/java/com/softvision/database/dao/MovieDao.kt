package com.softvision.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.softvision.database.model.MovieEntity
import org.jetbrains.annotations.TestOnly

@Dao
internal interface MovieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMovies(movies: List<MovieEntity>)

    @TestOnly
    @Query("SELECT * FROM movies")
    suspend fun getMovies(): List<MovieEntity>

    @Query("DELETE FROM movies")
    suspend fun clearMovies()
}