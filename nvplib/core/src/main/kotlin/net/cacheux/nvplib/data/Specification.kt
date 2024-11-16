package net.cacheux.nvplib.data

import net.cacheux.nvplib.utils.getIndexedString
import net.cacheux.nvplib.utils.getUnsignedShort
import java.nio.ByteBuffer

data class Specification(
    val serial: String = "",
    val partNumber: String = "",
    val hardwareRevision: String = "",
    val softwareRevision: String = ""
) {
    companion object {
        private const val SERIAL_NUMBER = 1
        private const val PART_NUMBER = 2
        private const val HW_VERSION = 3
        private const val SW_VERSION = 4

        fun fromByteBuffer(buffer: ByteBuffer): Specification {
            var serial = ""
            var partNumber = ""
            var softwareRevision = ""
            var hardwareRevision = ""

            val count = buffer.getUnsignedShort()
            buffer.getUnsignedShort() // size
            repeat(count) {
                val type = buffer.getUnsignedShort()
                buffer.getUnsignedShort() // component
                val value = buffer.getIndexedString()

                when (type) {
                    SERIAL_NUMBER -> serial = value
                    PART_NUMBER -> partNumber = value
                    HW_VERSION -> hardwareRevision = value
                    SW_VERSION -> softwareRevision = value
                }
            }
            return Specification(serial, partNumber, hardwareRevision, softwareRevision)
        }
    }
}
