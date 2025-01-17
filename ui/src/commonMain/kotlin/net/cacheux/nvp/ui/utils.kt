package net.cacheux.nvp.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import net.cacheux.nvplib.utils.StateFlowWrapper

data class StateWrapper<T>(
    val value: T,
    val setter: (T) -> Unit
)

@Composable
fun <T> StateFlowWrapper<T>.asStateWrapper()  = StateWrapper(
    value = this.content.collectAsState().value,
    setter = { this.set(it) }
)

fun <T> stateWrapper(initValue: T) = StateWrapper(
    value = initValue,
    setter = {}
)
