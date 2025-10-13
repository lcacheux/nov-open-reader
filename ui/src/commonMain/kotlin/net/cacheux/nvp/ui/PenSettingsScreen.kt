package net.cacheux.nvp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import net.cacheux.nvp.model.PenInfos
import net.cacheux.nvp.ui.ui.generated.resources.Res
import net.cacheux.nvp.ui.ui.generated.resources.back_button
import net.cacheux.nvp.ui.ui.generated.resources.delete_pen
import net.cacheux.nvp.ui.ui.generated.resources.delete_pen_warning
import net.cacheux.nvp.ui.ui.generated.resources.pen_color
import net.cacheux.nvp.ui.ui.generated.resources.pen_name
import net.cacheux.nvp.ui.ui.generated.resources.pen_settings
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

data class PenSettingsScreenParams(
    val onBack: () -> Unit = {},
    val penList: List<PenInfos> = listOf(),
    val onColorChanged: (serial: String, color: String) -> Unit = { _, _ -> },
    val onNameChanged: (serial: String, name: String) -> Unit = { _, _ -> },
    val onDeletePen: (serial: String) -> Unit = {},
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PenSettingsScreen(
    params: PenSettingsScreenParams = PenSettingsScreenParams()
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = params.onBack
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(
                            Res.string.back_button)
                        )
                    }
                },
                title = { Text(text = stringResource(Res.string.pen_settings)) },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
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

                ActionPreference(
                    label = stringResource(Res.string.delete_pen),
                    confirmMessage = stringResource(Res.string.delete_pen_warning),
                    action = { params.onDeletePen(pen.serial) },
                    testTag = "penDeletion${pen.serial}"
                )

                if (index < params.penList.size - 1) PrefDivider()
            }
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

@Preview
@Composable
fun PenSettingsScreenPreview() {
    PenSettingsScreen(
        PenSettingsScreenParams(
            penList = listOf(
                PenInfos(serial = "123456", name = "Blue Pen", color = "#0000FF"),
                PenInfos(serial = "654321", name = "Red Pen", color = "#FF0000")
            )
        )
    )
}
