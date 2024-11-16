package net.cacheux.nvp.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

class DatedItemTest {
    @Test
    fun testGroupByDate() {
        val items = listOf(
            testDoseGroup(testDateTime(10, 1, 12, 1), 10),
            testDoseGroup(testDateTime(12, 1, 13, 1), 12),
            testDoseGroup(testDateTime(20, 1, 14, 1), 14),
            testDoseGroup(testDateTime(12, 1, 13, 2), 16),
            testDoseGroup(testDateTime(20, 1, 14, 2), 18),
            testDoseGroup(testDateTime(8, 1, 13, 3), 20),
            testDoseGroup(testDateTime(12, 1, 14, 3), 22),
            testDoseGroup(testDateTime(20, 1, 14, 3), 24),
        )

        val result = items.groupByDate()
        assertEquals(3, result.keys.size)

        assertTrue(result.containsKey(testDateTime(0, 0, 0, 1)))
        assertTrue(result.containsKey(testDateTime(0, 0, 0, 2)))
        assertTrue(result.containsKey(testDateTime(0, 0, 0, 3)))

        assertEquals(3, result[testDateTime(0, 0, 0, 1)]?.size)
        assertEquals(2, result[testDateTime(0, 0, 0, 2)]?.size)
        assertEquals(3, result[testDateTime(0, 0, 0, 3)]?.size)
    }
}

fun testDoseGroup(date: Long, value: Int) = DoseGroup(
    doses = listOf(Dose(date, value))
)

fun testDateTime(
    hours: Int, min: Int, sec: Int,
    date: Int = 1, month: Int = 1, year: Int = 2024
) = Date(year, month, date, hours, min, sec).time