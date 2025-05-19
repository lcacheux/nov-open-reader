package net.cacheux.nvp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import net.cacheux.nvp.model.Dose
import net.cacheux.nvp.model.DoseGroup
import net.cacheux.nvp.model.testDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.Date

val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

@Composable
fun DoseGroupDetails(
    doseGroup: DoseGroup
) {
    Column(
        modifier = Modifier.background(Color.White)
    ) {
        Text(
            text = "Total: ${doseGroup.displayedTotal()}",
            fontWeight = FontWeight.Bold,
        )

        doseGroup.doses.forEach {
            DoseDetails(dose = it)
        }
    }
}

@Composable
fun DoseDetails(
    dose: Dose,
    modifier: Modifier = Modifier
) {
    Box(
        modifier
            .background(Color.White)
            .padding(4.dp),
    ) {
        Row {
            Text(
                modifier = Modifier.weight(1f),
                text = dose.displayedValue(),
                fontWeight = FontWeight.Bold,
                color = if (dose.ignored) Color.LightGray else Color.Black
            )

            Text(
                text = format.format(Date(dose.time)),
                fontStyle = FontStyle.Italic,
                color = if (dose.ignored) Color.LightGray else Color.DarkGray
            )
        }
    }
}


@Composable
@Preview
fun DoseDetailsPreview() {
    MaterialTheme {
        DoseDetails(dose = Dose(
            testDateTime(12, 12, 12),
            42
        ))
    }
}

@Composable
@Preview
fun DoseDetailsIgnoredPreview() {
    MaterialTheme {
        DoseDetails(dose = Dose(
            testDateTime(12, 12, 12),
            42,
            ignored = true
        ))
    }
}

@Composable
@Preview
fun DoseGroupDetailsPreview() {
    MaterialTheme {
        DoseGroupDetails(doseGroup = DoseGroup(
            doses = listOf(
                Dose(testDateTime(10, 0, 0), 2, true),
                Dose(testDateTime(10, 0, 10), 2, true),
                Dose(testDateTime(10, 0, 40), 2, true),
                Dose(testDateTime(10, 1, 30), 20),
                Dose(testDateTime(10, 1, 50), 16),
            )
        ))
    }
}