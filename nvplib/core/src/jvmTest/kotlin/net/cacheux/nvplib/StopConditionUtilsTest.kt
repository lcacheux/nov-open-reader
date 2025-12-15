package net.cacheux.nvplib

import net.cacheux.nvplib.data.InsulinDose
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class StopConditionUtilsTest {
    @Test
    fun testStopAfterDelay() {
        val condition = stopAfterDelay(
            "ABCD1234" to 123456,
            "EFGH5678" to 234567
        )

        assertFalse(condition("IJKL0000", insulineDoseList(1)))
        assertTrue(condition("ABCD1234", insulineDoseList(123458)))
        assertTrue(condition("EFGH5678", insulineDoseList(123455, 123678, 234560, 234590)))
        assertFalse(condition("EFGH5678", insulineDoseList(234000, 234010, 234020, 234030)))
    }

    private fun insulineDoseList(vararg time: Long) = time.map { InsulinDose(it, 0, 0) }
}
