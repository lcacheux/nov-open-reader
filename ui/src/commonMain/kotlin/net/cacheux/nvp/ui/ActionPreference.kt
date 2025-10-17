package net.cacheux.nvp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun ActionPreference(
    label: String,
    confirmMessage: String? = null,
    action: () -> Unit = {},
    testTag: String = ""
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable { confirmMessage?.let { showDialog = true } ?: action() }
            .padding(top = 8.dp, bottom = 8.dp)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .prefPadding()
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (showDialog) {
            ConfirmDialog(
                label = label,
                confirmMessage = confirmMessage,
                action = action,
                onDismiss = { showDialog = false }
            )
        }
    }
}
