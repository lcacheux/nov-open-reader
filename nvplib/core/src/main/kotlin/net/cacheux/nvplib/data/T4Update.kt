package net.cacheux.nvplib.data

import net.cacheux.nvplib.utils.putUnsignedByte
import net.cacheux.nvplib.utils.putUnsignedShort
import java.nio.ByteBuffer

class T4Update(
    private val bytes: ByteArray
) {
    companion object {
        private val CLA = 0x00
        private val UPDATE_COMMAND = 0xD6
    }

    fun toByteArray(): ByteArray {
        val b = ByteBuffer.allocate(bytes.size + 7)
        b.putUnsignedByte(CLA)
        b.putUnsignedByte(UPDATE_COMMAND)
        b.putUnsignedShort(0)
        b.putUnsignedByte(bytes.size + 2)
        b.putUnsignedShort(bytes.size)
        b.put(bytes)

        return b.array()
    }
}
