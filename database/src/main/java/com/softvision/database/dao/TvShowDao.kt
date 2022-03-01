package com.softvision.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.softvision.database.model.TvShowEntity
import org.jetbrains.annotations.TestOnly

@Dao
internal interface TvShowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTvShows(tvShows: List<TvShowEntity>)

    @TestOnly
    @Query("SELECT * FROM tv_shows")
    suspend fun getTvShows(): List<TvShowEntity>

    @Query("DELETE FROM tv_shows")
    suspend fun clearTvShows()

}