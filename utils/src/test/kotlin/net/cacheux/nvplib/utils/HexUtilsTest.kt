package net.cacheux.nvplib.utils

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class HexUtilsTest {

    @Test
    fun testParseHexDumpLine() {
        parseHexDumpLine("0x00000010 D1 03 FD 50 48 44 80 E7 00 00 F8 00 F6 80 02 01 ...PHD..........").let {
            assertEquals(16, it.index)
            assertEquals(16, it.data.size)
            assertArrayEquals("D103FD50484480E70000F800F6800201".hexToByteArray(), it.data)
        }

        parseHexDumpLine("0x00000030 AA BB CC ...").let {
            assertEquals(48, it.index)
            assertEquals(3, it.data.size)
            assertArrayEquals("AABBCC".hexToByteArray(), it.data)
        }

        parseHexDumpLine("0x00000000 90 00                                           ..").let {
            assertEquals(0, it.index)
            assertEquals(2, it.data.size)
            assertArrayEquals("9000".hexToByteArray(), it.data)
        }
    }

    @Test
    fun removeMultiple() {
        assertEquals("abde", "abcde".removeMultiple("c"))
        assertEquals("1357", "12345678".removeMultiple("2", "4", "6", "8"))
        assertEquals("foo", "f o o ".removeMultiple(" "))
    }
}