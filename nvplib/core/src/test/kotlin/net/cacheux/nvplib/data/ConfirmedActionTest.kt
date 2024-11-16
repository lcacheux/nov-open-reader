package net.cacheux.nvplib.data

import net.cacheux.nvplib.utils.hexToByteArray
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class ConfirmedActionTest {
    @Test
    fun testConfirmedAction() {
        val confirmedAction = ConfirmedAction.allSegment(
            handle = ConfirmedAction.STORE_HANDLE,
            type = DataApdu.MDC_ACT_SEG_GET_INFO
        )

        assertArrayEquals(
            "01 00 0C 0D 00 06 00 01 00 02 00 00".hexToByteArray(),
            confirmedAction.toByteArray()
        )
    }
}