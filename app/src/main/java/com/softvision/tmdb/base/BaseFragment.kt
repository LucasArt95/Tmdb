package com.softvision.tmdb.base

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseFragment<I : BaseIntent, A : BaseAction, R : BaseResult, S : BaseState> : Fragment() {

    protected abstract val viewModel: BaseViewModel<I, A, R, S>
    private lateinit var currentState: S

    protected abstract fun render(state: S)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeState()
    }


    /**
     * Initialize your view here
     *
     * Don't use onViewCreated since you will need
     * your view to be initialized before attempting to display a state
     * */
    abstract fun initView()

    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.state.collect {
                    currentState = it
                    render(it)
                }
            }
        }
    }

    protected open fun showErrorToast(error: Throwable) {
        val message = error.message ?: "Something went wrong"
        Timber.e(error)
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    fun getCurrentState() = currentState
}