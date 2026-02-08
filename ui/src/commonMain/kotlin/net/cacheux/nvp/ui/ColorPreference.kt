package net.cacheux.nvp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import net.cacheux.nvp.ui.ui.generated.resources.Res
import net.cacheux.nvp.ui.ui.generated.resources.cancel
import net.cacheux.nvp.ui.ui.generated.resources.ok
import net.cacheux.nvp.ui.ui.generated.resources.reset
import net.cacheux.nvp.ui.ui.generated.resources.select_color
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ColorPreference(
    label: String,
    value: StateWrapper<String>,
    testTag: String
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth()
            .clickable { showDialog = true }
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
            Text(
                text = value.value,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.weight(1f))
        if (value.value.isNotBlank()) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(16.dp)
                    .size(30.dp)
                    .background(value.value.hexToColor())
            )
        }
    }

    if (showDialog) {
        ColorInputDialog(
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
fun ColorInputDialog(
    initialValue: String,
    onDismissRequest: () -> Unit,
    onValueChange: (String) -> Unit
) {
    val controller = rememberColorPickerController()
    var hexCode by remember { mutableStateOf(initialValue) }

    fun isCodeValid() = hexCode.matches(Regex("[A-Fa-f0-9]{6}")) or hexCode.isEmpty()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = stringResource(Res.string.select_color)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {

                HsvColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(192.dp)
                        .padding(10.dp),
                    controller = controller,
                    initialColor = initialValue.hexToColor(),
                    onColorChanged = { colorEnvelope ->
                        hexCode = colorEnvelope.hexCode.substring(2)
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    TextField(
                        modifier = Modifier
                            .weight(5f)
                            .align(Alignment.CenterVertically)
                            .testTag("colorPrefValue"),
                        value = hexCode,
                        onValueChange = {
                            hexCode = it
                            if (isCodeValid()) {
                                controller.selectByColor(hexCode.hexToColor(), true)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.weight(0.5f))

                    TextButton(
                        modifier = Modifier
                            .align(Alignment.CenterVertically),
                        onClick = { controller.selectByColor(Color.White, true) }
                    ) {
                        Text(stringResource(Res.string.reset))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                AlphaTile(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    controller = controller,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onValueChange(hexCode) },
                enabled = isCodeValid()
            ) {
                Text(stringResource(Res.string.ok))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(stringResource(Res.string.cancel))
            }
        },

    )
}

fun String.hexToColor(): Color {
    if (length != 6) return Color.White
    return try {
        val red = substring(0, 2).toInt(16)
        val green = substring(2, 4).toInt(16)
        val blue = substring(4, 6).toInt(16)

        Color(red, green, blue)
    } catch (_: NumberFormatException) {
        Color.White
    }
}

@Preview
@Composable
fun ColorPreferencePreview() {
    MaterialTheme {
        ColorPreference(
            label = "Color",
            value = stateWrapper("ff0000"),
            testTag = ""
        )
    }
}
