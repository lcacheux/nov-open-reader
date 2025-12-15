package net.cacheux.nvplib.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeUtilsTest {
    @Test
    fun testRoundToSecond() {
        assertEquals(12345000L, 12345678L.roundToSecond())
    }
}
