package net.cacheux.nvplib.utils

import net.cacheux.bytonio.utils.reader
import org.junit.Assert.assertEquals
import org.junit.Test

class ByteUtilsTest {
    @Test
    fun getUnsignedByte() {
        val reader = byteArrayOf(0x03, 0xC9.toByte(), 0x11).reader()

        assertEquals(3, reader.getUnsignedByte())
        assertEquals(201, reader.getUnsignedByte())
        assertEquals(17, reader.getUnsignedByte())
    }
}
