package net.cacheux.nvplib.data

import net.cacheux.nvplib.annotations.IsShort
import net.cacheux.nvplib.utils.getUnsignedShort
import java.nio.ByteBuffer

data class Apdu(
    @IsShort val at: Int,
    val payload: Encodable
): Encodable() {
    companion object {
        const val AARQ = 0xE200
        const val AARE = 0xE300
        const val RLRQ = 0xE400
        const val RLRE = 0xE500
        const val ABRT = 0xE600
        const val PRST = 0xE700

        fun fromByteBuffer(buffer: ByteBuffer): Apdu {
            val at = buffer.getUnsignedShort()
            buffer.getUnsignedShort() // payloadLen

            val payload = when (at) {
                AARQ, AARE -> ARequest.fromByteBuffer(buffer)
                PRST -> DataApdu.fromByteBuffer(buffer)
                RLRQ, RLRE, ABRT -> Encodable()
                else -> throw IllegalStateException("Unknown at value $at")
            }

            return Apdu(at, payload)
        }
    }
}

fun Apdu.dataApdu(): DataApdu? {
    if (payload is DataApdu) return payload
    return null
}

fun Apdu.eventReport(): EventReport? {
    dataApdu()?.let { dataApdu ->
        if (dataApdu.payload is EventReport) return dataApdu.payload
    }
    return null
}
