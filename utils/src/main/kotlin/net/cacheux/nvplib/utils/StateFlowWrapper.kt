package net.cacheux.nvplib.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface StateFlowWrapper<T> {
    val content: StateFlow<T>
    val value: T
    val set: (T) -> Unit
}

class StateFlowWrapperImpl<T>(
    initValue: T,
    setter: (T) ->  Unit
): StateFlowWrapper<T> {
    private val mutableStateFlow = MutableStateFlow(initValue)
    override val content: StateFlow<T> = mutableStateFlow.asStateFlow()
    override val value = content.value
    override val set: (T) -> Unit = {
        mutableStateFlow.value = it
        setter(it)
    }
}

fun <T> stateFlowWrapper(initValue: T, setter: (T) -> Unit = {}) =
    StateFlowWrapperImpl(initValue, setter)
