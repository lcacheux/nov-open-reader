package net.cacheux.nvp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
import net.cacheux.nvp.ui.ui.generated.resources.group_iob
import net.cacheux.nvp.ui.ui.generated.resources.group_iob_delta
import net.cacheux.nvp.ui.ui.generated.resources.group_iob_details
import net.cacheux.nvp.ui.ui.generated.resources.group_iob_insulin_duration
import net.cacheux.nvp.ui.ui.generated.resources.group_iob_insulin_peak
import net.cacheux.nvp.ui.ui.generated.resources.hours_suffix
import net.cacheux.nvp.ui.ui.generated.resources.minutes_suffix
import net.cacheux.nvp.ui.ui.generated.resources.settings
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
    val groupDose: StateWrapper<Boolean> = stateWrapper(true),
    val groupDelay: StateWrapper<Int> = stateWrapper(60),
    val autoIgnoreEnabled: StateWrapper<Boolean> = stateWrapper(true),
    val autoIgnoreValue: StateWrapper<Int> = stateWrapper(2),

    val groupIoB: StateWrapper<Boolean> = stateWrapper(false),
    val insulinPeak: StateWrapper<Int> = stateWrapper(75),
    val delta: StateWrapper<Int> = stateWrapper(15),
    val insulinDuration: StateWrapper<Int> = stateWrapper(5)

)

@Composable
fun SettingsScreen(
    params: SettingsScreenParams = SettingsScreenParams()
) {
    Column {
        Row {
            IconButton(
                onClick = params.onBack,
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back_button))
            }

            Text(
                text = stringResource(Res.string.settings),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
                    .align(Alignment.CenterVertically)
            )
        }

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

        ExpandableSwitch(
            label = stringResource(Res.string.group_iob),
            subLabel = stringResource(Res.string.group_iob_details),
            state = params.groupIoB,
            testTag = "groupIobSwitch"
        ) {
            PrefDivider()
            IntPreference(
                label = stringResource(Res.string.group_iob_insulin_peak),
                value = params.insulinPeak,
                suffix = stringResource(Res.string.minutes_suffix),
                testTag = "iobInsulinPeakPref"
            )

            PrefDivider()
            IntPreference(
                label = stringResource(Res.string.group_iob_delta),
                value = params.delta,
                suffix = stringResource(Res.string.minutes_suffix),
                testTag = "iobDeltaPref"
            )

            PrefDivider()
            IntPreference(
                label = stringResource(Res.string.group_iob_insulin_duration),
                value = params.insulinDuration,
                suffix = stringResource(Res.string.hours_suffix),
                testTag = "iobInsulinDurationPref"
            )
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
