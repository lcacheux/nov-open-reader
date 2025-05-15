package net.cacheux.nvp.ui

import androidx.compose.runtime.Composable

@Composable
expect fun BackHandlerWrapper(
    enabled: Boolean = true,
    onBack: () -> Unit
)
