package net.cacheux.nvplib.data

import net.cacheux.nvplib.annotations.IsByte
import net.cacheux.nvplib.annotations.IsInt
import net.cacheux.nvplib.annotations.IsShort
import org.junit.Assert.assertArrayEquals
import org.junit.Test

data class EncodableItem(
    @IsInt var id: Int,
    @IsShort var value: Int,
    @IsByte var byte: Int,
    var array: ByteArray,
    val subItem: SubItem
): Encodable()

data class SubItem(
    val content: ByteArray
): Encodable()

class EncodableTest {
    @Test
    fun testEncodable() {
        val testItem = EncodableItem(
            98765,
            677,
            0x24,
            byteArrayOf(0xAA.toByte(), 0xBB.toByte(), 0xCC.toByte()),
            SubItem(byteArrayOf(0x11, 0x22, 0x33))
        )

        val result = testItem.toByteArray()
        assertArrayEquals(
            byteArrayOf(
                0x00, 0x01, 0x81.toByte(), 0xCD.toByte(),
                0x02, 0xA5.toByte(),
                0x24,
                0x00, 0x03,
                0xAA.toByte(), 0xBB.toByte(), 0xCC.toByte(),
                0x00, 0x05, 0x00, 0x03,
                0x11, 0x22, 0x33
            ), result
        )
    }
}
