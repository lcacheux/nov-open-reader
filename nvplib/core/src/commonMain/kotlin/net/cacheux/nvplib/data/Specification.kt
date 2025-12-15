package net.cacheux.nvplib.data

import net.cacheux.bytonio.utils.ByteArrayReader
import net.cacheux.nvplib.utils.getIndexedString
import net.cacheux.nvplib.utils.getUnsignedShort

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

        fun fromByteArrayReader(reader: ByteArrayReader): Specification {
            var serial = ""
            var partNumber = ""
            var softwareRevision = ""
            var hardwareRevision = ""

            val count = reader.getUnsignedShort()
            reader.getUnsignedShort() // size
            repeat(count) {
                val type = reader.getUnsignedShort()
                reader.getUnsignedShort() // component
                val value = reader.getIndexedString()

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
