package net.cacheux.nvp.app

import net.cacheux.nvp.app.utils.csvLineToDose
import net.cacheux.nvp.app.utils.csvToDoseList
import net.cacheux.nvp.app.utils.toCsv
import net.cacheux.nvp.model.Dose
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.TimeZone

class CsvTest {
    @Before
    fun init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"))
    }

    @Test
    fun testToCsv() {

        val pen1 = "ABCD1234"
        val pen2 = "ABCD5678"

        val list = listOf(
            testDose(1, 12, 0, 0, 20, pen1),
            testDose(1, 12, 0, 10, 40, pen1),
            testDose(1, 12, 0, 20, 160, pen1),
            testDose(1, 12, 0, 40, 160, pen1),

            testDose(1, 12, 10, 0, 20, pen1),
            testDose(1, 12, 10, 10, 60, pen1),
            testDose(1, 12, 10, 20, 400, pen1),

            testDose(1, 12, 10, 30, 20, pen2),
            testDose(1, 12, 10, 40, 520, pen2)
        )

        with(list.toCsv()) {
            println(this)
            assert(startsWith("Serial;Timestamp;Time;Value"))
            assert(contains("ABCD1234;1704110420000;2024-01-01T13:00:20.000+0100;160"))
            assert(contains("ABCD5678;1704111040000;2024-01-01T13:10:40.000+0100;520"))
            assertEquals(list.size * 3 + 3, count { it == ';'})
            assertEquals(list.size + 1, count { it == '\n' })
        }
    }

    @Test
    fun testCsvToDoseList() {
        val csv = """
Serial;Timestamp;Time;Value
ABCD1234;1704110400000;2024-01-01T13:00:00.000+0100;20
ABCD1234;1704110410000;2024-01-01T13:00:10.000+0100;40
ABCD1234;1704110420000;2024-01-01T13:00:20.000+0100;160
ABCD1234;1704110440000;2024-01-01T13:00:40.000+0100;160
ABCD1234;1704111000000;2024-01-01T13:10:00.000+0100;20
ABCD1234;1704111010000;2024-01-01T13:10:10.000+0100;60
ABCD1234;1704111020000;2024-01-01T13:10:20.000+0100;400
ABCD5678;1704111030000;2024-01-01T13:10:30.000+0100;20
ABCD5678;1704111040000;2024-01-01T13:10:40.000+0100;520
        """.trimIndent()

        with(csv.csvToDoseList()) {
            assertEquals(9, size)
            assertEquals("ABCD1234", get(0).serial)
            assertEquals("ABCD1234", get(6).serial)
            assertEquals("ABCD5678", get(7).serial)
            assertEquals(1704110410000, get(1).time)
            assertEquals(1704111010000, get(5).time)
            assertEquals(1704111020000, get(6).time)
            assertEquals(20, get(0).value)
            assertEquals(160, get(2).value)
            assertEquals(520, get(8).value)

        }
    }

    @Test
    fun testCsvLineToDose() {
        with("ABCD1234;1704110420000;2024-01-01T13:00:20.000+0100;160".csvLineToDose()) {
            assertEquals(1704110420000, time)
            assertEquals(160, value)
            assertEquals("ABCD1234", serial)
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCsvLineToDoseWrongFormat() {
        "dummydata".csvLineToDose()
    }

    @Test(expected = IllegalArgumentException::class)
    fun testCsvLineToDoseWrongNumber() {
        "ABCD1234;1704110420000;2024-01-01T13:00:20.000+0100;TEST".csvLineToDose()
    }
}

fun generateTimestamp(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): Long {
    val localDateTime = LocalDateTime.of(year, month, day, hour, minute, second, 0)
    return localDateTime.toEpochSecond(ZoneOffset.UTC) * 1000
}

private fun testDose(
    day: Int, hour: Int, minute: Int, second: Int,
    value: Int, serial: String
) = Dose(
    time = generateTimestamp(2024, 1, day, hour, minute, second),
    value = value,
    serial = serial,
)
