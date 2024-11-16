package net.cacheux.nvp.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

class DoseGroupTest {

    companion object {
        const val SERIAL1 = "ABCD1234"
        const val SERIAL2 = "EFGH5678"

        val singleDoseList1 = listOf(
            Dose(dateTime(10, 0, 0), 20),
            Dose(dateTime(10, 0, 10), 20),
            Dose(dateTime(10, 0, 40), 20),
            Dose(dateTime(10, 1, 30), 200),
            Dose(dateTime(10, 1, 50), 160),
        )

        val singleDoseList2 = listOf(
            Dose(dateTime(10, 0, 0), 20),
            Dose(dateTime(10, 0, 10), 20),
            Dose(dateTime(10, 0, 40), 20),
            Dose(dateTime(10, 1, 30), 200),
            Dose(dateTime(10, 1, 35), 20),
            Dose(dateTime(10, 1, 50), 160),
        )

        val only2valuesList = listOf(
            Dose(dateTime(10, 0, 0), 20),
            Dose(dateTime(10, 0, 10), 20),
            Dose(dateTime(10, 0, 40), 20)
        )

        val doseList = listOf(
            Dose(dateTime(10, 0, 0), 20),
            Dose(dateTime(10, 0, 10), 20),
            Dose(dateTime(10, 0, 40), 20),
            Dose(dateTime(10, 1, 30), 200),
            Dose(dateTime(10, 1, 50), 160),

            Dose(dateTime(13, 1, 50), 20),
            Dose(dateTime(13, 2, 10), 300),

            Dose(dateTime(15, 1, 10), 300),
            Dose(dateTime(15, 1, 16), 20),

            Dose(dateTime(20, 10, 10), 10),
            Dose(dateTime(20, 11, 0), 160),
            Dose(dateTime(20, 11, 2), 20),

            Dose(dateTime(22, 11, 2), 20),
        )

        val multiplePensDoseList = listOf(
            Dose(dateTime(10, 0, 0), 20, serial = SERIAL1),
            Dose(dateTime(10, 0, 10), 20, serial = SERIAL1),
            Dose(dateTime(10, 0, 40), 20, serial = SERIAL1),
            Dose(dateTime(10, 1, 30), 200, serial = SERIAL1),
            Dose(dateTime(10, 1, 50), 160, serial = SERIAL1),

            Dose(dateTime(10, 2, 10), 20, serial = SERIAL2),
            Dose(dateTime(10, 2, 20), 20, serial = SERIAL2),
            Dose(dateTime(10, 2, 30), 340, serial = SERIAL2),
        )
    }

    @Test
    fun toDoseListWithIgnoredFlag() {
        val result = singleDoseList1.toDoseListWithIgnoredFlag(DoseGroupConfig())

        assertTrue(result[0].ignored)
        assertTrue(result[1].ignored)
        assertTrue(result[2].ignored)
        assertFalse(result[3].ignored)
        assertFalse(result[4].ignored)

        val result2 = singleDoseList2.toDoseListWithIgnoredFlag(DoseGroupConfig())

        assertTrue(result2[0].ignored)
        assertTrue(result2[1].ignored)
        assertTrue(result2[2].ignored)
        assertFalse(result2[3].ignored)
        assertFalse(result2[4].ignored)
        assertFalse(result2[5].ignored)

        val result3 = only2valuesList.toDoseListWithIgnoredFlag(DoseGroupConfig())

        assertTrue(result3[0].ignored)
        assertTrue(result3[1].ignored)
        assertFalse(result3[2].ignored)
    }

    @Test
    fun testCreateDoseGroup() {
        val group = DoseGroup.createDoseGroups(doseList)
        assertEquals(5, group.size)
        assertEquals(dateTime(10, 1, 50), group[0].getTime())
        assertEquals(360, group[0].getTotal())
        assertEquals(dateTime(13, 2, 10), group[1].getTime())
        assertEquals(300, group[1].getTotal())
        assertEquals(320, group[2].getTotal())
        assertEquals(180, group[3].getTotal())
        assertEquals(20, group[4].getTotal())
    }

    @Test
    fun testCreateDoseGroupWithMultiplePens() {
        val group = DoseGroup.createDoseGroups(multiplePensDoseList)
        assertEquals(2, group.size)
        assertEquals(dateTime(10, 1, 50), group[0].getTime())
        assertEquals(SERIAL1, group[0].getSerial())
        assertEquals(dateTime(10, 2, 30), group[1].getTime())
        assertEquals(SERIAL2, group[1].getSerial())
    }
}

fun dateTime(hours: Int, min: Int, sec: Int) =
    Date(2024, 1, 1, hours, min, sec).time
