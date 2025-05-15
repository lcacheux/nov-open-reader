package net.cacheux.nvp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun BackHandlerWrapper(
    enabled: Boolean,
    onBack: () -> Unit
) = BackHandler(enabled) {
    onBack()
}
