package net.cacheux.nvplib.data

import net.cacheux.bytonio.BinaryDeserializer
import net.cacheux.bytonio.BinarySerializable
import net.cacheux.bytonio.BinarySerializer
import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.annotations.Deserializer
import net.cacheux.bytonio.annotations.EncodeAsShort
import net.cacheux.bytonio.annotations.Serializer
import net.cacheux.bytonio.utils.ByteArrayReader
import net.cacheux.bytonio.utils.reader
import net.cacheux.nvplib.data.DataApdu.Companion.CONFIRMED_ACTION_CHOSEN
import net.cacheux.nvplib.data.DataApdu.Companion.CONFIRMED_EVENT_REPORT_CHOSEN
import net.cacheux.nvplib.data.DataApdu.Companion.GET_CHOSEN
import net.cacheux.nvplib.data.DataApdu.Companion.MDC_ACT_SEG_GET_INFO
import net.cacheux.nvplib.data.DataApdu.Companion.MDC_ACT_SEG_TRIG_XFER
import net.cacheux.nvplib.data.DataApdu.Companion.SGET_CHOSEN
import net.cacheux.nvplib.generated.TriggerSegmentDataXferDeserializer
import java.nio.ByteBuffer

@DataObject
@Serializer(DataApduSerializer::class)
@Deserializer(DataApduDeserializer::class)
data class DataApdu(
    @EncodeAsShort val invokeId: Int,
    @EncodeAsShort val dchoice: Int,
    val payload: BinarySerializable? = null
): BinarySerializable {
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
    }

    override fun getBinarySize(): Int {
        return 6 + (payload?.let { it.getBinarySize() + 2 } ?: 0)
    }

    override fun toByteArray(): ByteArray {
        val payloadSize = (payload?.let {
            it.getBinarySize() + 2
        } ?: 0)
        return ByteBuffer.allocate(getBinarySize()).apply {
            putShort((payloadSize + 4).toShort())
            putShort(invokeId.toShort())
            putShort(dchoice.toShort())
            payload?.let {
                putShort(payload.getBinarySize().toShort())
                put(payload.toByteArray())
            }
        }.array()
    }
}

object DataApduSerializer: BinarySerializer<DataApdu> {
    override fun getBinarySize(data: DataApdu): Int {
        return 6 + (data.payload?.let { it.getBinarySize() + 2 } ?: 0)
    }

    override fun toByteArray(data: DataApdu): ByteArray {
        val payloadSize = (data.payload?.let {
            it.getBinarySize() + 2
        } ?: 0)
        return ByteBuffer.allocate(data.getBinarySize()).apply {
            putShort((payloadSize + 4).toShort())
            putShort(data.invokeId.toShort())
            putShort(data.dchoice.toShort())
            data.payload?.let {
                putShort(data.payload.getBinarySize().toShort())
                put(data.payload.toByteArray())
            }
        }.array()
    }
}

object DataApduDeserializer: BinaryDeserializer<DataApdu> {
    override fun fromByteArray(byteArray: ByteArray) = fromByteArrayReader(byteArray.reader())

    override fun fromByteArrayReader(reader: ByteArrayReader): DataApdu {
        reader.readShort() // olen
        val invokeId = reader.readShort()
        val dchoice = reader.readShort()
        reader.readShort() // dlen

        val payload = when (dchoice) {
            CONFIRMED_ACTION_CHOSEN -> {
                reader.readShort() // handle
                val actionType = reader.readShort()
                reader.readShort() // actionLen
                when (actionType) {
                    MDC_ACT_SEG_GET_INFO -> SegmentInfoList.fromByteArrayReader(reader)
                    MDC_ACT_SEG_TRIG_XFER -> TriggerSegmentDataXferDeserializer.fromByteArrayReader(reader)
                    else -> null
                }
            }

            CONFIRMED_EVENT_REPORT_CHOSEN -> EventReport.fromByteArrayReader(reader)

            SGET_CHOSEN, GET_CHOSEN -> {
                reader.readShort() // handle
                val count = reader.readShort()
                reader.readShort() // len
                FullSpecification.fromAttributes((1 .. count).map { AttributeDeserializer.fromByteArrayReader(reader) })
            }

            else -> null
        }

        return DataApdu(invokeId, dchoice, payload)
    }
}
