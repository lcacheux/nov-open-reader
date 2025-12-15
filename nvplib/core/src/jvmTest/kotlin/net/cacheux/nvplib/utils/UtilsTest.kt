package net.cacheux.nvplib.utils

import org.junit.Assert.assertArrayEquals
import org.junit.Test

class UtilsTest {
    @Test
    fun decomposeNumberTest() {
        assertArrayEquals(arrayOf(10, 10, 10, 10, 10, 5), decomposeNumber(55, 10).toTypedArray())
        assertArrayEquals(arrayOf(7, 7, 1), decomposeNumber(15, 7).toTypedArray())
        assertArrayEquals(arrayOf(3, 3, 3, 1), decomposeNumber(10, 3).toTypedArray())
        assertArrayEquals(arrayOf(1), decomposeNumber(1, 5).toTypedArray())
    }
}