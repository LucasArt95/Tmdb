package com.softvision.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.softvision.database.dao.GenresDao
import com.softvision.database.dao.MovieDao
import com.softvision.database.dao.ResultsDao
import com.softvision.database.dao.TvShowDao
import com.softvision.database.model.*

@Database(
    entities = [MovieEntity::class, TvShowEntity::class, GenreEntity::class, MovieResultEntity::class, TvShowResultEntity::class, MovieResultCrossRef::class, TvShowResultCrossRef::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
internal abstract class TmdbDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

    abstract fun tvShowsDao(): TvShowDao

    abstract fun resultsDao(): ResultsDao

    abstract fun genresDao(): GenresDao

    companion object {

        @Volatile
        private var INSTANCE: TmdbDatabase? = null
        private const val DATABASE_NAME = "tmdb_database"

        fun getDatabase(context: Context): TmdbDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, TmdbDatabase::class.java, DATABASE_NAME).build()
                INSTANCE = instance
                instance
            }
        }
    }

}
