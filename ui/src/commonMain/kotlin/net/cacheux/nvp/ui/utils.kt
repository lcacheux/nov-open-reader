package net.cacheux.nvp.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
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

fun <T> stateWrapper(initValue: T, setter: (T) -> Unit = {}) = StateWrapper(
    value = initValue,
    setter = setter
)

@Composable
fun isInDarkMode() =
    MaterialTheme.colorScheme.background == darkColorScheme().background
