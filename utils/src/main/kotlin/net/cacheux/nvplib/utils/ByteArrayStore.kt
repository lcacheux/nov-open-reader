package net.cacheux.nvplib.utils

import java.io.InputStream
import java.io.OutputStream

class ByteArrayStore {
    private val byteList = mutableListOf<ByteArray>()
    val content: List<ByteArray> = byteList

    companion object {
        fun fromInputStream(input: InputStream): ByteArrayStore {
            return ByteArrayStore().apply {
                input.reader().useLines { lines ->
                    var current: ByteArray? = null
                    lines.forEach { line ->
                        try {
                            if (line.isNotBlank()) {
                                val hexLine = parseHexDumpLine(line)
                                if (hexLine.index == 0) {
                                    current?.let { addByteArray(it) }
                                    current = hexLine.data
                                } else {
                                    current = current?.plus(hexLine.data)
                                }
                            }
                        } catch (_: IllegalArgumentException) {}
                    }
                    current?.let { addByteArray(it) }
                }
            }
        }
    }

    fun addByteArray(byteArray: ByteArray) = byteList.add(byteArray)

    fun toOutputStream(output: OutputStream) {
        output.writer().use { writer ->
            byteList.forEach {
                writer.append(it.dumpHex()).appendLine()
            }
        }
    }

    fun clear() {
        byteList.clear()
    }
}
