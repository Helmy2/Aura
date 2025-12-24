package com.example.aura.shared.core.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * A result of a reduce operation, containing the new state
 * and any side effects to be emitted.
 */
data class ReducerResult<S, E>(
    val newState: S,
    val effects: List<E> = emptyList(),
)

/**
 * Base class for ViewModels that follow the MVI pattern.
 * Removed TimeCapsule for simplicity.
 */
abstract class BaseViewModel<S, I, E>(
    initialState: S
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    private val _effect = Channel<E>()
    val effect: Flow<E> = _effect.receiveAsFlow()

    /**
     * The core logic for state reduction.
     * Concrete ViewModels must implement this function to handle state changes.
     */
    protected abstract fun reduce(oldState: S, intent: I): ReducerResult<S, E>

    /**
     * Sends an intent to be processed.
     */
    fun sendIntent(intent: I) {
        viewModelScope.launch {
            val oldState = _state.value
            val result = reduce(oldState, intent)

            // Send effects first (optional, but often good for navigation triggers)
            result.effects.forEach { effect ->
                _effect.send(effect)
            }

            // Update state
            if (result.newState != oldState) {
                _state.value = result.newState
            }
        }
    }
}