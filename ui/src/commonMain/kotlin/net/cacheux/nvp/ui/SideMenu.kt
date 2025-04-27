package net.cacheux.nvp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.cacheux.nvp.model.PenInfos
import net.cacheux.nvp.ui.ui.generated.resources.Res
import net.cacheux.nvp.ui.ui.generated.resources.all
import net.cacheux.nvp.ui.ui.generated.resources.pen_list
import net.cacheux.nvp.ui.ui.generated.resources.pen_settings
import net.cacheux.nvp.ui.ui.generated.resources.settings
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

data class SideMenuParams(
    val penList: List<PenInfos> = listOf(),
    val selectedPen: String? = null,
    val onItemClick: (String?) -> Unit = {},
    val onPenSettingsClick: () -> Unit = {},
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
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        SideMenuPen(null, params.selectedPen, onClick = params.onItemClick)
        params.penList.forEach {
            SideMenuPen(it, params.selectedPen, onClick = params.onItemClick)
        }

        NavigationDrawerItem(
            modifier = Modifier.padding(8.dp),
            label = { Text(text = stringResource(Res.string.pen_settings)) },
            selected = false,
            onClick = {
                params.onPenSettingsClick()
            }
        )

        HorizontalDivider()

        NavigationDrawerItem(
            modifier = Modifier.padding(8.dp),
            label = { Text(text = stringResource(Res.string.settings)) },
            selected = false,
            onClick = {
                params.onSettingsClick()
            }
        )
    }
}

@Composable
fun SideMenuPen(
    pen: PenInfos?,
    selectedPen: String? = null,
    onClick: (String?) -> Unit = {}
) {
    NavigationDrawerItem(
        modifier = Modifier
            .padding(8.dp),
        label = {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = pen?.displayName() ?: stringResource(Res.string.all))
                Spacer(modifier = Modifier.weight(1f))
                pen?.color?.let {
                    if (it.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(it.hexToColor())
                        )
                    }
                }
            }
        },
        selected = pen?.serial == selectedPen,
        onClick = { onClick(pen?.serial) }
    )
}

@Preview
@Composable
fun SideMenuPreview() {
    MaterialTheme {
        SideMenu(SideMenuParams(listOf(
            PenInfos(serial = "ABCD1234"),
            PenInfos(serial = "EFGH5678", name = "Lantus", color = "dd0000")
        )))
    }
}

@Preview
@Composable
fun SideMenuPenPreview() {
    MaterialTheme {
        SideMenuPen(PenInfos(serial = "ABCD1234"))
    }
}
