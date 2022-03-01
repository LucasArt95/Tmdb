package com.softvision.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.softvision.model.media.MediaType
import com.softvision.model.media.SortType


internal class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromIntegerToSortType(value: Int?): SortType? {
        return SortType.values().firstOrNull { it.ordinal == value }
    }

    @TypeConverter
    fun sortTypeToInteger(sortType: SortType?): Int? {
        return sortType?.ordinal
    }

    @TypeConverter
    fun fromIntegerToMediaType(value: Int): MediaType {
        return MediaType.values().first { it.ordinal == value }
    }

    @TypeConverter
    fun mediaTypeToInteger(mediaType: MediaType): Int {
        return mediaType.ordinal
    }

    @TypeConverter
    fun fromJsonToStringList(value: String): List<String> {
        return gson.fromJson(value, object : TypeToken<List<String>>() {}.type)
    }

    @TypeConverter
    fun stringListToJson(list: List<String>?): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromJsonToIntegerList(value: String): List<Int> {
        return gson.fromJson(value, object : TypeToken<List<Int>>() {}.type)
    }

    @TypeConverter
    fun integerListToJson(list: List<Int>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromJsonToLongList(value: String): List<Long> {
        return gson.fromJson(value, object : TypeToken<List<Long>>() {}.type)
    }

    @TypeConverter
    fun longListToJson(list: List<Long>): String {
        return gson.toJson(list)
    }
}