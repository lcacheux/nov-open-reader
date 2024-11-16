/**
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.cacheux.nvplib.utils

/**
 * Adapted from the HexDump Android class.
 */

private val HEX_DIGITS = charArrayOf(
    '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
)

private const val LINESIZE = 16

fun ByteArray.dumpHex() = dumpHexString(this)

fun dumpHexString(array: ByteArray?): String {
    return if (array == null) "<null>" else dumpHex(array, 0, array.size)
}

fun dumpHex(array: ByteArray?, offset: Int, length: Int): String {
    if (array == null) return "<null>"
    val result = StringBuilder()

    val line = ByteArray(LINESIZE)
    var lineIndex = 0

    result.append("\n0x").append(toHexString(offset))

    for (i in offset until offset + length) {
        if (lineIndex == LINESIZE) {
            result.append(" ")
            for (j in 0 until LINESIZE) {
                if (line[j] > ' '.code.toByte() && line[j] < '~'.code.toByte()) {
                    result.append(String(line, j, 1))
                } else {
                    result.append(".")
                }
            }
            result.append("\n0x")
            result.append(toHexString(i))
            lineIndex = 0
        }
        val b = array[i]

        result.append(" ")
            .append(HEX_DIGITS[b.toInt() ushr 4 and 0x0F])
            .append(HEX_DIGITS[b.toInt() and 0x0F])

        line[lineIndex++] = b
    }

    if (lineIndex != LINESIZE) {
        var count = (LINESIZE - lineIndex) * 3
        count++
        repeat(count) {
            result.append(" ")
        }
        repeat(lineIndex) { i ->
            if (line[i] > ' '.code.toByte() && line[i] < '~'.code.toByte()) {
                result.append(String(line, i, 1))
            } else {
                result.append(".")
            }
        }
    }
    return result.toString()
}

fun toHexString(array: ByteArray, offset: Int = 0, length: Int = array.size): String {
    val buf = CharArray(length * 2)
    var bufIndex = 0
    for (i in offset until offset + length) {
        val b = array[i]
        buf[bufIndex++] = HEX_DIGITS[b.toInt() ushr 4 and 0x0F]
        buf[bufIndex++] = HEX_DIGITS[b.toInt() and 0x0F]
    }
    return String(buf)
}

fun toHexString(i: Int): String {
    return toHexString(toByteArray(i))
}

fun toByteArray(i: Int): ByteArray {
    val array = ByteArray(4)
    array[3] = (i and 0xFF).toByte()
    array[2] = (i shr 8 and 0xFF).toByte()
    array[1] = (i shr 16 and 0xFF).toByte()
    array[0] = (i shr 24 and 0xFF).toByte()
    return array
}

private fun toByte(c: Char): Int {
    if (c in '0'..'9') return c.code - '0'.code
    if (c in 'A'..'F') return c.code - 'A'.code + 10
    if (c in 'a'..'f') return c.code - 'a'.code + 10
    throw RuntimeException("Invalid hex char '$c'")
}

fun stringToByteArray(hexString: String): ByteArray {
    return hexString.chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}

fun String.removeMultiple(vararg items: String): String {
    return items.fold(this) { acc, s -> acc.replace(s, "") }
}

fun String.hexToByteArray() = stringToByteArray(removeMultiple("\n", " ", "\t"))

data class HexDumpLine(
    val index: Int,
    val data: ByteArray
)

fun parseHexDumpLine(input: String): HexDumpLine {
    val len = (input.length - 11) / 4
    if (len < 0 || len > 16) throw IllegalArgumentException("Wrong hexdump input")
    if (!input.startsWith("0x")) throw IllegalArgumentException("Wrong hexdump input")
    val index = input.substring(2, 10).toInt(16)
    var array = byteArrayOf()
    repeat(len) {
        try {
            input.substring(11 + it * 3, 13 + it * 3).let { num ->
                if (num.isNotBlank())
                    array += num.toInt(16).toByte()
            }
        } catch (_: NumberFormatException) {}
    }
    return HexDumpLine(index, array)
}
