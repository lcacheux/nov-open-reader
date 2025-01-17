package net.cacheux.nvp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ExpandableSwitch(
    label: String,
    subLabel: String? = null,
    state: StateWrapper<Boolean> = stateWrapper(false),
    testTag: String = "",
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .prefPadding()
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge
                )
                subLabel?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp),
                        softWrap = true
                    )
                }

            }

            Switch(
                checked = state.value,
                onCheckedChange = { state.setter(it) },
                modifier = Modifier
                    .padding(8.dp)
                    .testTag(testTag)
            )
        }

        if (state.value) {
            content()
        }
    }
}

@Preview
@Composable
fun ExpandableSwitchPreview() {
    MaterialTheme {
        ExpandableSwitch(
            label = "Expand this",
            subLabel = "Sub label",
            state = stateWrapper(true)
        ) {
            HorizontalDivider(modifier = Modifier.padding(start = 4.dp, end = 4.dp))

            TextPreference(
                label = "Preference 1",
                value = "Value 1"
            )

            HorizontalDivider(modifier = Modifier.padding(start = 4.dp, end = 4.dp))

            TextPreference(
                label = "Preference 2",
                value = "Value 2"
            )
        }
    }
}
