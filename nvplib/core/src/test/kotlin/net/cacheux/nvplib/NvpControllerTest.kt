package net.cacheux.nvplib

import net.cacheux.nvplib.data.PenResult
import net.cacheux.nvplib.testing.TestingDataReader
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class NvpControllerTest {

    @Test
    fun testDataRead() {
        val file = File("nvp_datatest.txt")
        val controller = NvpController(TestingDataReader(file.inputStream()))
        val result = controller.dataRead()

        assertTrue(result is PenResult.Success)
        (result as PenResult.Success).data.let { data ->
            assertEquals("ABERGX", data.serial)
            assertEquals(96, data.doseList.size)
            assertEquals(6206432L, data.startTime)

            val currentTime = System.currentTimeMillis()

            assertArrayEquals(
                intArrayOf(20, 20, 100, 160, 140, 20),
                data.doseList.map { it.units }.subList(0, 6).toIntArray()
            )

            assertArrayEquals(
                // There is always a small gap of a few millis so round values to second to ensure
                // values won't change
                longArrayOf(-86, -87, -90, -92, -260474, -260480),
                data.doseList.map { (it.time - currentTime) / 1000 }.subList(0, 6).toLongArray()
            )
        }
    }

    @Test
    fun testStopCondition() {
        val file = File("nvp_datatest.txt")
        val controller = NvpController(TestingDataReader(file.inputStream()))
        val result = controller.dataRead { _, list ->
            list.find { it.time > 1726330347000 }?.let { true } ?: false
        }

        assertTrue(result is PenResult.Success)
        (result as PenResult.Success).data.let { data ->
            assertEquals("ABERGX", data.serial)
            assertEquals(18, data.doseList.size)
        }
    }
}
