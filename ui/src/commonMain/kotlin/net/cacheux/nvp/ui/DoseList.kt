@file:OptIn(ExperimentalFoundationApi::class)

package net.cacheux.nvp.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.cacheux.nvp.model.DoseGroup
import net.cacheux.nvp.model.groupByDate
import net.cacheux.nvp.model.testDateTime
import net.cacheux.nvp.model.testDoseGroup
import net.cacheux.nvp.ui.ui.generated.resources.Res
import net.cacheux.nvp.ui.ui.generated.resources.empty_list_message
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.Date

val headerDate = SimpleDateFormat("dd/MM/YYYY")

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DoseList(
    items: List<DoseGroup>,
    currentDoseGroup: DoseGroup? = null,
    onDoseClick: (DoseGroup) -> Unit = {}
) {
    if (items.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(Res.string.empty_list_message),
                fontSize = 18.sp,
                fontStyle = FontStyle.Italic
            )
        }
    } else {
        val grouped = items.groupByDate()

        LazyColumn {
            grouped.forEach { (date, doses) ->
                stickyHeader {
                    DoseListHeader(date)
                }
                items(doses) { item ->
                    DoseListItem(
                        dose = item,
                        isCurrent = item == currentDoseGroup,
                        onClick = onDoseClick
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun DoseListHeader(
    date: Long
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .padding(horizontal = 4.dp)
    ) {
        Text(
            text = headerDate.format(Date(date)),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun DoseListItem(
    dose: DoseGroup,
    isCurrent: Boolean = false,
    onClick: (DoseGroup) -> Unit  = {}
) {
    val format = SimpleDateFormat("HH:mm:ss")

    Box(
        modifier = Modifier
            .background(
                color = dose.doses.first().color.hexToColor().copy(alpha = 0.5f)
            )
            .fillMaxWidth()
            .clickable { onClick(dose) }
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 4.dp)
        ) {
            Text(
                fontSize = 14.sp,
                fontStyle = FontStyle.Italic,
                text = format.format(Date(dose.getTime()))
            )
            Text(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                text = dose.displayedTotal()
            )

            if (isCurrent) {
                DoseGroupDetails(
                    doseGroup = dose,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp, end = 4.dp, bottom = 8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun DoseListPreview() {
    val current = testDoseGroup(testDateTime(12, 1, 14), 14)
    val items = listOf(
        testDoseGroup(testDateTime(12, 1, 12), 12),
        testDoseGroup(testDateTime(12, 1, 13), 13),
        current,
        testDoseGroup(testDateTime(12, 1, 14, date = 2), 14),
        testDoseGroup(testDateTime(12, 1, 15, date = 2), 14),
        testDoseGroup(testDateTime(12, 1, 14, date = 3), 14),
    )
    DoseList(
        items,
        currentDoseGroup = current
    )
}

@Preview
@Composable
fun DoseListItemPreview() {
    val dose = testDoseGroup(testDateTime(12, 2, 23), 42)
    DoseDisplay(dose)
}
