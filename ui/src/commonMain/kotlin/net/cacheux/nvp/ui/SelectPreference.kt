package net.cacheux.nvp.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.cacheux.nvp.ui.ui.generated.resources.Res
import net.cacheux.nvp.ui.ui.generated.resources.cancel
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun <T> SelectPreference(
    label: String,
    value: StateWrapper<T>,
    options: List<Pair<T, String>>,
    testTag: String = ""
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .prefPadding()
                .fillMaxWidth()
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge
            )
            val currentLabel = options.firstOrNull { it.first == value.value }?.second
                ?: value.value?.toString().orEmpty()
            Text(
                text = currentLabel,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }

    if (showDialog) {
        SelectPreferencePopup(
            label, value, options, { showDialog = false }
        )
    }
}

@Composable
fun <T> SelectPreferencePopup(
    label: String,
    value: StateWrapper<T>,
    options: List<Pair<T, String>>,
    onDismiss: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = label) },
        text = {
            Column {
                options.forEach { (optValue, optLabel) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                value.setter(optValue)
                                onDismiss()
                            }
                            .padding(8.dp)
                    ) {
                        RadioButton(
                            modifier = Modifier
                                .align(CenterVertically),
                            selected = value.value == optValue,
                            onClick = {
                                value.setter(optValue)
                                onDismiss()
                            }
                        )
                        Text(
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .align(CenterVertically),
                            text = optLabel,
                            fontSize = 16.sp,
                        )
                    }
                }
            }
        },
        confirmButton = {

        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel))
            }
        }
    )
}

@Preview
@Composable
fun SelectPreferencePreview() {
    MaterialTheme {
        SelectPreference(
            "Visual mode",
            stateWrapper("light"),
            listOf("light" to "Light", "dark" to "Dark"),

        )
    }
}

@Preview
@Composable
fun SelectPreferencePopupPreview() {
    MaterialTheme {
        SelectPreferencePopup(
            "Visual mode",
            stateWrapper("light"),
            listOf("light" to "Light", "dark" to "Dark"),
        )
    }
}