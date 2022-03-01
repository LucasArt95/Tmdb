package com.softvision.tmdb.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

abstract class BaseViewModel<I : BaseIntent, A : BaseAction, R : BaseResult, S : BaseState> : ViewModel() {

    protected abstract val initialState: S

    private val intents = Channel<I>()

    @FlowPreview
    @ExperimentalCoroutinesApi
    val state: StateFlow<S> by lazy {
        intents.consumeAsFlow()
            .map { actionFromIntent(it) }
            .flatMapMerge { resultFromAction(it) }
            .scan(initialState, ::reduce)
            .stateIn(viewModelScope + Dispatchers.IO, SharingStarted.Lazily, initialState)
    }

    abstract fun actionFromIntent(intent: I): A

    abstract suspend fun resultFromAction(action: A): Flow<R>

    abstract fun reduce(previousState: S, result: R): S

    fun submitIntent(intent: I) {
        viewModelScope.launch {
            intents.send(intent)
        }
    }

}