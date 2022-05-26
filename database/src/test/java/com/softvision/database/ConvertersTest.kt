package com.softvision.database

import com.softvision.model.media.MediaType
import com.softvision.model.media.SortType
import org.junit.Assert
import org.junit.Test

class ConvertersTest {

    private val converters = Converters()

    @Test
    fun serializationOfSortTypeShouldReturnOrdinal() {
        val sortType = SortType.values().random()
        val serializedValue = converters.sortTypeToInteger(sortType)
        Assert.assertEquals(sortType.ordinal, serializedValue)
    }

    @Test
    fun deserializationOfSortTypeShouldReturnCorrectValue() {
        val sortType = SortType.values().random()
        val deserializedValue = converters.fromIntegerToSortType(sortType.ordinal)
        Assert.assertEquals(sortType, deserializedValue)
    }

    @Test
    fun serializationOfMediaTypeShouldReturnOrdinal() {
        val mediaType = MediaType.values().random()
        val serializedValue = converters.mediaTypeToInteger(mediaType)
        Assert.assertEquals(mediaType.ordinal, serializedValue)
    }

    @Test
    fun deserializationOfMediaTypeShouldReturnCorrectValue() {
        val mediaType = MediaType.values().random()
        val deserializedValue = converters.fromIntegerToMediaType(mediaType.ordinal)
        Assert.assertEquals(mediaType, deserializedValue)
    }

    @Test
    fun serializationOfStringListShouldStringJson() {
        val stringList = listOf("1", "2", "3")
        val expectedJson = "[\"1\",\"2\",\"3\"]"
        val serializedValue = converters.stringListToJson(stringList)
        Assert.assertEquals(expectedJson, serializedValue)
    }

    @Test
    fun deserializationOfStringListJsonShouldStringList() {
        val stringList = listOf("1", "2", "3")
        val json = "[\"1\",\"2\",\"3\"]"
        val deserializedValue = converters.fromJsonToStringList(json)
        Assert.assertEquals(stringList, deserializedValue)
    }

    @Test
    fun serializationOfLongListShouldStringJson() {
        val longList = listOf(1L, 2L, 3L)
        val expectedJson = "[1,2,3]"
        val serializedValue = converters.longListToJson(longList)
        Assert.assertEquals(expectedJson, serializedValue)
    }

    @Test
    fun deserializationOfLongListJsonShouldStringList() {
        val longList = listOf(1L, 2L, 3L)
        val json = "[1,2,3]"
        val deserializedValue = converters.fromJsonToLongList(json)
        Assert.assertEquals(longList, deserializedValue)
    }

    @Test
    fun serializationOfIntegerListShouldStringJson() {
        val integerList = listOf(1, 2, 3)
        val expectedJson = "[1,2,3]"
        val serializedValue = converters.integerListToJson(integerList)
        Assert.assertEquals(expectedJson, serializedValue)
    }

    @Test
    fun deserializationOfIntegerListJsonShouldStringList() {
        val integerList = listOf(1, 2, 3)
        val json = "[1,2,3]"
        val deserializedValue = converters.fromJsonToIntegerList(json)
        Assert.assertEquals(integerList, deserializedValue)
    }
}