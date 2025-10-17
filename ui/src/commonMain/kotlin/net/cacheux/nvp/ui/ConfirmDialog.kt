package net.cacheux.nvp.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import net.cacheux.nvp.ui.ui.generated.resources.Res
import net.cacheux.nvp.ui.ui.generated.resources.cancel
import net.cacheux.nvp.ui.ui.generated.resources.ok
import org.jetbrains.compose.resources.stringResource

@Composable
fun ConfirmDialog(
    label: String,
    confirmMessage: String?,
    action: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = label) },
        text = { Text(text = confirmMessage ?: "") },
        confirmButton = {
            TextButton(onClick = {
                action()
                onDismiss()
            }) {
                Text(stringResource(Res.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel))
            }
        }
    )
}
