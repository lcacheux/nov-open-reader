package net.cacheux.nvp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.cacheux.nvp.ui.ui.generated.resources.Res
import net.cacheux.nvp.ui.ui.generated.resources.auto_ignore
import net.cacheux.nvp.ui.ui.generated.resources.auto_ignore_details
import net.cacheux.nvp.ui.ui.generated.resources.auto_ignore_value
import net.cacheux.nvp.ui.ui.generated.resources.auto_ignore_value_suffix
import net.cacheux.nvp.ui.ui.generated.resources.back_button
import net.cacheux.nvp.ui.ui.generated.resources.group_delay
import net.cacheux.nvp.ui.ui.generated.resources.group_delay_suffix
import net.cacheux.nvp.ui.ui.generated.resources.group_doses
import net.cacheux.nvp.ui.ui.generated.resources.group_doses_details
import net.cacheux.nvp.ui.ui.generated.resources.settings
import net.cacheux.nvp.ui.ui.generated.resources.theme
import net.cacheux.nvp.ui.ui.generated.resources.theme_dark
import net.cacheux.nvp.ui.ui.generated.resources.theme_light
import net.cacheux.nvp.ui.ui.generated.resources.theme_system
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun PrefDivider() =
    HorizontalDivider(modifier = Modifier.padding(start = 8.dp, end = 8.dp))

fun Modifier.prefPadding() = padding(
    start = 16.dp, end = 16.dp,
    top = 8.dp, bottom = 8.dp
)

data class SettingsScreenParams(
    val onBack: () -> Unit = {},
    val theme: StateWrapper<Int> = stateWrapper(0),
    val groupDose: StateWrapper<Boolean> = stateWrapper(true),
    val groupDelay: StateWrapper<Int> = stateWrapper(60),
    val autoIgnoreEnabled: StateWrapper<Boolean> = stateWrapper(true),
    val autoIgnoreValue: StateWrapper<Int> = stateWrapper(2),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    params: SettingsScreenParams = SettingsScreenParams()
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
                title = { Text(text = stringResource(Res.string.settings)) },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            
            SelectPreference(
                label = stringResource(Res.string.theme),
                value = params.theme,
                options = listOf(
                    0 to stringResource(Res.string.theme_light),
                    1 to stringResource(Res.string.theme_dark),
                    2 to stringResource(Res.string.theme_system),
                    )
            )

            PrefDivider()

            ExpandableSwitch(
                label = stringResource(Res.string.group_doses),
                subLabel = stringResource(Res.string.group_doses_details),
                state = params.groupDose,
                testTag = "groupDosesSwitch"
            ) {
                PrefDivider()
                IntPreference(
                    label = stringResource(Res.string.group_delay),
                    value = params.groupDelay,
                    suffix = stringResource(Res.string.group_delay_suffix),
                    testTag = "groupDelayPref"
                )

                PrefDivider()
                ExpandableSwitch(
                    label = stringResource(Res.string.auto_ignore),
                    subLabel = stringResource(Res.string.auto_ignore_details),
                    state = params.autoIgnoreEnabled,
                    testTag = "autoIgnoreSwitch"
                ) {
                    PrefDivider()
                    IntPreference(
                        label = stringResource(Res.string.auto_ignore_value),
                        value = params.autoIgnoreValue,
                        suffix = stringResource(Res.string.auto_ignore_value_suffix),
                        testTag = "autoIgnoreValuePref"
                    )
                }
            }
        }
    }
}

@Composable
fun TextPreference(
    label: String,
    value: String
) {
    Column(
        modifier = Modifier.prefPadding()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Preview
@Composable
fun PreferenceScreenPreview() {
    MaterialTheme {
        SettingsScreen()
    }
}

@Preview
@Composable
fun TextPreferencePreview() {
    MaterialTheme {
        TextPreference(
            label = "Group delay",
            value = "60 seconds"
        )
    }
}
