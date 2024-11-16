package net.cacheux.nvplib.utils

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.nio.ByteBuffer

data class TestClass(
    var id: Int,
    var value: Short,
    var byte: Byte,
    var array: ByteArray,
)

class ByteUtilsTest {
    @Test
    fun getUnsignedByte() {
        val buffer = ByteBuffer.wrap(byteArrayOf(0x03, 0xC9.toByte(), 0x11))

        assertEquals(3, buffer.getUnsignedByte())
        assertEquals(201, buffer.getUnsignedByte())
        assertEquals(17, buffer.getUnsignedByte())
    }

    @Test
    fun testDataClassSerialization() {
        val item = TestClass(
            98765,
            677,
            0x24,
            byteArrayOf(0xAA.toByte(), 0xBB.toByte(), 0xCC.toByte())
        )

        val result = encodeToByteArray(item)
        assertArrayEquals(
            byteArrayOf(
                0x00, 0x01, 0x81.toByte(), 0xCD.toByte(),
                0x02, 0xA5.toByte(),
                0x24,
                0xAA.toByte(), 0xBB.toByte(), 0xCC.toByte()
            ), result)
    }
}