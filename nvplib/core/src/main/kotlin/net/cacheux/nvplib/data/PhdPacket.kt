package net.cacheux.nvplib.data

import net.cacheux.nvplib.utils.getUnsignedByte
import net.cacheux.nvplib.utils.putUnsignedByte
import java.nio.ByteBuffer

data class PhdPacket(
    val opcode: Byte = -1,
    val typeLen: Int = -1,
    val payloadLen: Int = -1,
    val headerLen: Int? = null,
    val header: ByteArray? = null,
    val seq: Int = -1,
    val chk: Int = 0,
    val content: ByteArray
) {
    companion object {
        private const val MB = 1 shl 7
        private const val ME = 1 shl 6
        private const val CF = 1 shl 5
        private const val SR = 1 shl 4
        private const val IL = 1 shl 3

        private const val WELL_KNOWN = 1

        fun fromByteBuffer(buffer: ByteBuffer): PhdPacket {
            val opcode = buffer.getUnsignedByte()
            val typeLen = buffer.getUnsignedByte()
            val payloadLen = buffer.getUnsignedByte() - 1
            val hasId = opcode and (1 shl 3) != 0
            val headerLen = if (hasId) {
                buffer.getUnsignedByte()
            } else 0
            val protoId = ByteArray(3)
            buffer.get(protoId)
            val header = if (hasId) {
                ByteArray(headerLen).apply { buffer.get(this) }
            } else null
            val chk = buffer.getUnsignedByte()
            val realLen = if (buffer.remaining() < payloadLen) buffer.remaining() else payloadLen
            val inner = ByteArray(realLen).apply {
                buffer.get(this)
            }

            return PhdPacket(opcode.toByte(), typeLen, realLen, headerLen, header, seq = (chk and 0x0f), chk = chk, inner)
        }
    }

    fun toByteArray(): ByteArray {
        val ilen = content.size
        val idLen = header?.size ?: 0
        val hasId = idLen > 0
        val b = ByteBuffer.allocate(ilen + 7)
        b.putUnsignedByte(MB or ME or SR or (if(hasId) IL else 0) or WELL_KNOWN)
        b.putUnsignedByte(3)
        b.putUnsignedByte(ilen + 1)
        if (hasId) {
            b.put(header)
        }
        b.put("PHD".toByteArray())
        b.putUnsignedByte(seq and 0x0F or 0x80 or chk)
        if (ilen > 0) {
            b.put(content)
        }
        return b.array()
    }
}
