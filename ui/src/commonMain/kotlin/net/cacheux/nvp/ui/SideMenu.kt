package net.cacheux.nvp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.cacheux.nvp.ui.ui.generated.resources.Res
import net.cacheux.nvp.ui.ui.generated.resources.all
import net.cacheux.nvp.ui.ui.generated.resources.pen_list
import net.cacheux.nvp.ui.ui.generated.resources.settings
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

data class SideMenuParams(
    val penList: List<String> = listOf(),
    val selectedPen: String? = null,
    val onItemClick: (String?) -> Unit = {},
    val onSettingsClick: () -> Unit = {},
)

@Composable
fun SideMenu(
    params: SideMenuParams
) {
    ModalDrawerSheet(
        modifier = Modifier.width(240.dp)
    ) {
        Text(
            text = stringResource(Res.string.pen_list),
            modifier = Modifier.padding(16.dp)
        )
        HorizontalDivider()
        SideMenuPen(null, params.selectedPen, onClick = params.onItemClick)
        params.penList.forEach {
            SideMenuPen(it, params.selectedPen, onClick = params.onItemClick)
        }

        HorizontalDivider()

        NavigationDrawerItem(
            modifier = Modifier.padding(8.dp),
            label = { Text(text = stringResource(Res.string.settings)) },
            selected = false,
            onClick = params.onSettingsClick
        )
    }
}

@Composable
fun SideMenuPen(
    serial: String?,
    selectedPen: String? = null,
    onClick: (String?) -> Unit = {}
) {
    NavigationDrawerItem(
        modifier = Modifier.padding(8.dp),
        label = { Text(text = serial ?: stringResource(Res.string.all)) },
        selected = serial == selectedPen,
        onClick = { onClick(serial) }
    )
}

@Preview
@Composable
fun SideMenuPreview() {
    MaterialTheme {
        SideMenu(SideMenuParams(listOf("ABCD1234", "EFGH5678")))
    }
}

@Preview
@Composable
fun SideMenuPenPreview() {
    MaterialTheme {
        SideMenuPen(serial = "ABCD1234")
    }
}
