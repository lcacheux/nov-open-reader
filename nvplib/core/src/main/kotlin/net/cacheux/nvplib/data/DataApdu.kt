package net.cacheux.nvplib.data

import net.cacheux.nvplib.annotations.IsShort
import net.cacheux.nvplib.utils.getUnsignedShort
import java.nio.ByteBuffer

data class DataApdu(
    @IsShort val invokeId: Int,
    @IsShort val dchoice: Int,
    val payload: Encodable? = null
): Encodable() {
    companion object {
        const val EVENT_REPORT_CHOSEN = 0x0100
        const val CONFIRMED_EVENT_REPORT_CHOSEN = 0x0101
        const val SCONFIRMED_EVENT_REPORT_CHOSEN = 0x0201
        const val GET_CHOSEN = 0x0203
        const val SGET_CHOSEN = 0x0103
        const val CONFIRMED_ACTION = 0x0107
        const val CONFIRMED_ACTION_CHOSEN = 0x0207
        const val MDC_ACT_SEG_GET_INFO = 0x0C0D
        const val MDC_ACT_SEG_TRIG_XFER = 0x0C1C

        fun fromByteBuffer(buffer: ByteBuffer): DataApdu {
            buffer.getUnsignedShort() // olen
            val invokeId = buffer.getUnsignedShort()
            val dchoice = buffer.getUnsignedShort()
            buffer.getUnsignedShort() // dlen

            val payload = when (dchoice) {
                CONFIRMED_ACTION_CHOSEN -> {
                    buffer.getUnsignedShort() // handle
                    val actionType = buffer.getUnsignedShort()
                    buffer.getUnsignedShort() // actionLen
                    when (actionType) {
                        MDC_ACT_SEG_GET_INFO -> SegmentInfoList.fromByteBuffer(buffer)
                        MDC_ACT_SEG_TRIG_XFER -> TriggerSegmentDataXfer.fromByteBuffer(buffer)
                        else -> null
                    }
                }

                CONFIRMED_EVENT_REPORT_CHOSEN -> EventReport.fromByteBuffer(buffer)

                SGET_CHOSEN, GET_CHOSEN -> {
                    buffer.getUnsignedShort() // handle
                    val count = buffer.getUnsignedShort()
                    buffer.getUnsignedShort() // len
                    FullSpecification.fromAttributes((1 .. count).map { Attribute.fromByteBuffer(buffer) })
                }

                else -> null
            }

            return DataApdu(invokeId, dchoice, payload)
        }
    }

    override fun encodedSize(): Int {
        return 6 + (payload?.let { it.encodedSize() + 2 } ?: 0)
    }

    override fun toByteArray(): ByteArray {
        val payloadSize = (payload?.let {
            it.encodedSize() + 2
        } ?: 0)
        return ByteBuffer.allocate(encodedSize()).apply {
            putShort((payloadSize + 4).toShort())
            putShort(invokeId.toShort())
            putShort(dchoice.toShort())
            payload?.let {
                putShort(payload.encodedSize().toShort())
                put(payload.toByteArray())
            }
        }.array()
    }
}
