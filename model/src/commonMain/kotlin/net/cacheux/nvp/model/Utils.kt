package net.cacheux.nvp.model

import java.time.Instant
import java.time.ZoneId
import java.util.GregorianCalendar

fun timestampToDate(timestamp: Long): Long {
    val instant = Instant.ofEpochMilli(timestamp)
    val localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate()
    return localDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

fun testDoseGroup(date: Long, value: Int) = DoseGroup(
    doses = listOf(Dose(date, value))
)

fun testDateTime(hours: Int, min: Int, sec: Int, date: Int = 1, month: Int = 1, year: Int = 2024) =
    GregorianCalendar.getInstance().apply {
        set(year, month, date, hours, min, sec)
    }.time.time

/**
 * Generate a full week of credible data for two pens
 */
fun generateDoseData(serial1: String, serial2: String): List<Dose> {
    val result = mutableListOf<Dose>()
    (1 .. 7).forEach { day ->
        // Dose around 8:00
        (1..9).random().let { minute ->
            result.add(
                Dose(
                    time = testDateTime(8, minute, 9, day),
                    value = 20,
                    serial = serial1
                )
            )
            result.add(
                Dose(
                    time = testDateTime(8, minute, 34, day),
                    value = 160,
                    serial = serial1
                )
            )
        }

        // Dose around 12:00
        (11..16).random().let { minute ->
            result.add(
                Dose(
                    time = testDateTime(12, minute, 30, day),
                    value = 20,
                    serial = serial1
                )
            )
            result.add(
                Dose(
                    time = testDateTime(12, minute, 45, day),
                    value = 200,
                    serial = serial1
                )
            )
        }

        // Dose around 19:00
        (8..14).random().let { minute ->
            result.add(
                Dose(
                    time = testDateTime(19, minute, 5, day),
                    value = 20,
                    serial = serial2
                )
            )
            result.add(
                Dose(
                    time = testDateTime(19, minute, 18, day),
                    value = 560,
                    serial = serial2
                )
            )

            result.add(
                Dose(
                    time = testDateTime(19, minute + 1, 10, day),
                    value = 20,
                    serial = serial1
                )
            )
            result.add(
                Dose(
                    time = testDateTime(19, minute + 1, 30, day),
                    value = 240,
                    serial = serial1
                )
            )
        }
    }
    return result
}
