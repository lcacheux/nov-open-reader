package net.cacheux.nvp.ui

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import net.cacheux.nvp.ui.ui.generated.resources.Res
import net.cacheux.nvp.ui.ui.generated.resources.export_csv
import net.cacheux.nvp.ui.ui.generated.resources.import_csv
import net.cacheux.nvp.ui.ui.generated.resources.load_raw_data
import net.cacheux.nvp.ui.ui.generated.resources.save_raw_data
import org.jetbrains.compose.resources.stringResource

data class MainDropdownMenuActions(
    val onLoadingClick: () -> Unit = {},
    val onSaveStore: () -> Unit = {},
    val onExportCsv: () -> Unit = {},
    val onImportCsv: () -> Unit = {}
) {
    fun and(action: () -> Unit) = MainDropdownMenuActions(
        { onLoadingClick(); action() },
        { onSaveStore(); action() },
        { onExportCsv(); action() },
        { onImportCsv(); action() }
    )
}

@Composable
fun MainDropdownMenu(
    opened: Boolean = true,
    onDismiss: () -> Unit = {},
    loadingFileAvailable: Boolean = false,
    storeAvailable: Boolean = false,
    actions: MainDropdownMenuActions = MainDropdownMenuActions()
) {
    DropdownMenu(
        expanded = opened,
        onDismissRequest = onDismiss
    ) {
        if (loadingFileAvailable) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(Res.string.load_raw_data))
                },
                onClick = actions.onLoadingClick
            )
        }
        DropdownMenuItem(
            enabled = storeAvailable,
            text = {
                Text(text = stringResource(Res.string.save_raw_data))
            },
            onClick = actions.onSaveStore
        )
        DropdownMenuItem(
            text = {
                Text(text = stringResource(Res.string.export_csv))
            },
            onClick = actions.onExportCsv
        )
        DropdownMenuItem(
            text = {
                Text(text = stringResource(Res.string.import_csv))
            },
            onClick = actions.onImportCsv
        )
    }
}
