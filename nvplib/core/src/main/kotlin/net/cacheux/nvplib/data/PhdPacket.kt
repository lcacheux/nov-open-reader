package net.cacheux.nvplib.data

import net.cacheux.bytonio.utils.ByteArrayReader
import net.cacheux.bytonio.utils.writer
import net.cacheux.nvplib.utils.getUnsignedByte
import net.cacheux.nvplib.utils.putUnsignedByte

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

        fun fromByteArrayReader(reader: ByteArrayReader): PhdPacket {
            val opcode = reader.getUnsignedByte()
            val typeLen = reader.getUnsignedByte()
            val payloadLen = reader.getUnsignedByte() - 1
            val hasId = opcode and (1 shl 3) != 0
            val headerLen = if (hasId) {
                reader.getUnsignedByte()
            } else 0
            val protoId = reader.readByteArray(3)
            val header = if (hasId) {
                reader.readByteArray(headerLen.toInt())
            } else null
            val chk = reader.getUnsignedByte()
            val realLen = if (reader.remaining() < payloadLen.toInt()) reader.remaining() else payloadLen.toInt()
            val inner = reader.readByteArray(realLen)

            return PhdPacket(opcode.toByte(), typeLen,
                realLen, headerLen,
                header, seq = (chk and 0x0f), chk = chk, inner
            )
        }
    }

    fun toByteArray(): ByteArray {
        val ilen = content.size
        val idLen = header?.size ?: 0
        val hasId = idLen > 0
        return ByteArray(ilen + 7).writer().apply {
            putUnsignedByte(MB or ME or SR or (if(hasId) IL else 0) or WELL_KNOWN)
            putUnsignedByte(3)
            putUnsignedByte(ilen + 1)
            if (hasId && header != null) {
                writeByteArray(header)
            }
            writeByteArray("PHD".toByteArray())
            putUnsignedByte(seq and 0x0F or 0x80 or chk)
            if (ilen > 0) {
                writeByteArray(content)
            }
        }.byteArray
    }
}
