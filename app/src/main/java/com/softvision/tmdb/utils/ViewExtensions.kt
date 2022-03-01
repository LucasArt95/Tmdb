package com.softvision.tmdb.utils

import androidx.appcompat.widget.SearchView
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object ViewExtensions {

    @ExperimentalCoroutinesApi
    fun SearchView.queryFlow(): Flow<String?> =
        callbackFlow {
            val callback = object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    trySendBlocking(newText)
                    return true
                }
            }
            this@queryFlow.setOnQueryTextListener(callback)
            awaitClose { this@queryFlow.setOnQueryTextListener(null) }
        }

}