package net.cacheux.nvp.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import net.cacheux.nvp.model.IoB
import net.cacheux.nvp.ui.ui.generated.resources.Res
import net.cacheux.nvp.ui.ui.generated.resources.active_insulin
import net.cacheux.nvp.ui.ui.generated.resources.insulin_on_board
import net.cacheux.nvp.ui.utils.formatUnit
import org.jetbrains.compose.resources.stringResource


@Composable
fun InsulinOnBoard(
    iob: IoB?
) {
    Row {
        Text(stringResource(Res.string.insulin_on_board))
        Text(iob?.remaining.formatUnit())
        Spacer(modifier = Modifier.width(8.dp))
        Text(stringResource( Res.string.active_insulin))
        Text(iob?.current.formatUnit())
    }
}