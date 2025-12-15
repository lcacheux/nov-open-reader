package net.cacheux.nvplib.data

import net.cacheux.bytonio.utils.writer
import net.cacheux.nvplib.utils.putUnsignedByte
import net.cacheux.nvplib.utils.putUnsignedShort

class T4Update(
    private val bytes: ByteArray
) {
    companion object {
        private val CLA = 0x00
        private val UPDATE_COMMAND = 0xD6
    }

    fun toByteArray() =
        ByteArray(bytes.size + 7).writer().apply {
            putUnsignedByte(CLA)
            putUnsignedByte(UPDATE_COMMAND)
            putUnsignedShort(0)
            putUnsignedByte(bytes.size + 2)
            putUnsignedShort(bytes.size)
            writeByteArray(bytes)
        }.byteArray
}
