package net.cacheux.nvp.ui

import androidx.compose.runtime.Composable

@Composable
actual fun BackHandlerWrapper(
    enabled: Boolean,
    onBack: () -> Unit
) {
    // No back button in desktop mode
}
