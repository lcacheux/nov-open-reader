package net.cacheux.nvp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.cacheux.nvp.model.PenInfos
import net.cacheux.nvp.ui.ui.generated.resources.Res
import net.cacheux.nvp.ui.ui.generated.resources.back_button
import net.cacheux.nvp.ui.ui.generated.resources.pen_color
import net.cacheux.nvp.ui.ui.generated.resources.pen_name
import net.cacheux.nvp.ui.ui.generated.resources.pen_settings
import org.jetbrains.compose.resources.stringResource

data class PenSettingsScreenParams(
    val onBack: () -> Unit = {},
    val penList: List<PenInfos> = listOf(),
    val onColorChanged: (serial: String, color: String) -> Unit = { _, _ -> },
    val onNameChanged: (serial: String, name: String) -> Unit = { _, _ -> }
)

@Composable
fun PenSettingsScreen(
    params: PenSettingsScreenParams = PenSettingsScreenParams()
) {
    Column {
        Row {
            IconButton(
                onClick = params.onBack,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(
                    Res.string.back_button)
                )
            }

            Text(
                text = stringResource(Res.string.pen_settings),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
                    .align(Alignment.CenterVertically)
            )
        }

        params.penList.forEachIndexed { index, pen ->
            PenHeader(pen)

            StringPreference(
                label = stringResource(Res.string.pen_name),
                value = stateWrapper(pen.name) { value ->
                    params.onNameChanged(pen.serial, value)
                },
                testTag = "penNameProp${pen.serial}"
            )

            ColorPreference(
                label = stringResource(Res.string.pen_color),
                value = stateWrapper(pen.color) { value ->
                    params.onColorChanged(pen.serial, value)
                },
                testTag = "penColorProp${pen.serial}"
            )

            if (index < params.penList.size - 1) PrefDivider()
        }
    }
}

@Composable
fun PenHeader(
    penInfos: PenInfos
) {
    Column(
        modifier = Modifier
            .prefPadding()
            .fillMaxWidth()
    ) {
        Text(
            text = penInfos.serial,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}