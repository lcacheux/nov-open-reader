package net.cacheux.nvplib.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class ByteStringLoaderTest {
    @Test
    fun testLoadByteString() {
        val input = "01 0a 0b ac 12 1F".removeMultiple(" ")
        val output = stringToByteArray(input)

        assertEquals(6, output.size)
        assertEquals((0x01).toByte(), output[0])
        assertEquals((0xac).toByte(), output[3])
        assertEquals((0x1f).toByte(), output[5])
    }
}