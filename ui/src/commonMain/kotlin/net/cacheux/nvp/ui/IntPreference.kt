package net.cacheux.nvp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

@Composable
fun IntPreference(
    label: String,
    value: StateWrapper<Int>,
    suffix: String = "",
    testTag: String
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .prefPadding()
            .fillMaxWidth()
            .clickable { showDialog = true }
            .testTag(testTag)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "${value.value}$suffix",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
        )
    }

    if (showDialog) {
        IntInputDialog(
            label = label,
            initialValue = value.value,
            onDismissRequest = { showDialog = false },
            onValueChange = {
                value.setter(it)
                showDialog = false
            }
        )
    }
}


@Composable
fun IntInputDialog(
    label: String,
    initialValue: Int,
    onDismissRequest: () -> Unit,
    onValueChange: (Int) -> Unit
) {
    var inputValue by remember { mutableStateOf(initialValue.toString()) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = label) },
        text = {
            TextField(
                value = inputValue,
                onValueChange = { inputValue = it }
            )
        },
        confirmButton = {
            TextButton(onClick = {
                onValueChange(inputValue.toIntOrNull() ?: initialValue)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}
