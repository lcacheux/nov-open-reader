package net.cacheux.nvp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.cacheux.nvp.model.Dose
import net.cacheux.nvp.model.DoseGroup
import net.cacheux.nvp.model.testDateTime
import net.cacheux.nvp.ui.ui.generated.resources.Res
import net.cacheux.nvp.ui.ui.generated.resources.delete_dose_warning
import net.cacheux.nvp.ui.ui.generated.resources.delete_selected
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.Date

val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

@Composable
fun DoseGroupDetails(
    doseGroup: DoseGroup,
    modifier: Modifier = Modifier,
    onDoseDeletion: (List<Dose>) -> Unit = {}
) {
    var editMode by remember { mutableStateOf(false) }

    val selected = remember { mutableStateListOf<Dose>() }

    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.background)
            .testTag("doseGroupDetails")
            .pointerInput(doseGroup) {
                detectTapGestures(
                    onLongPress = { editMode = true }
                )
            }
    ) {
        doseGroup.doses.forEach {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (editMode) {
                    Checkbox(
                        modifier = Modifier.
                            testTag("doseCheck${it.id}"),
                        checked = selected.contains(it),
                        onCheckedChange = { checked ->
                            if (checked) {
                                selected.add(it)
                            } else {
                                selected.remove(it)
                            }
                        }
                    )
                }
                DoseDetails(dose = it, modifier = Modifier.weight(1f))
            }

        }

        if (editMode) {
            var showConfirm by remember { mutableStateOf(false) }

            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = {
                    showConfirm = true
                },
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                enabled = selected.isNotEmpty()
            ) {
                Text(stringResource(Res.string.delete_selected))
            }

            if (showConfirm) {
                ConfirmDialog(
                    label = stringResource(Res.string.delete_selected),
                    confirmMessage = stringResource(Res.string.delete_dose_warning),
                    action = {
                        onDoseDeletion(selected.toList())
                        selected.clear()
                    },
                    onDismiss = { showConfirm = false }
                )
            }
        }
    }
}

@Composable
fun DoseDetails(
    dose: Dose,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(4.dp),
    ) {
        Row {
            Text(
                modifier = Modifier.weight(1f),
                text = dose.displayedValue(),
                fontWeight = FontWeight.Bold,
                color = if (dose.ignored)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = format.format(Date(dose.time)),
                fontStyle = FontStyle.Italic,
                color = if (dose.ignored)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }
}


@Composable
@Preview
fun DoseDetailsPreview() {
    MaterialTheme {
        DoseDetails(dose = Dose(
            testDateTime(12, 12, 12),
            42
        ))
    }
}

@Composable
@Preview
fun DoseDetailsIgnoredPreview() {
    MaterialTheme {
        DoseDetails(dose = Dose(
            testDateTime(12, 12, 12),
            42,
            ignored = true
        ))
    }
}

@Composable
@Preview
fun DoseGroupDetailsPreview() {
    MaterialTheme {
        DoseGroupDetails(doseGroup = DoseGroup(
            doses = listOf(
                Dose(testDateTime(10, 0, 0), 2, true),
                Dose(testDateTime(10, 0, 10), 2, true),
                Dose(testDateTime(10, 0, 40), 2, true),
                Dose(testDateTime(10, 1, 30), 20),
                Dose(testDateTime(10, 1, 50), 16),
            )
        ))
    }
}