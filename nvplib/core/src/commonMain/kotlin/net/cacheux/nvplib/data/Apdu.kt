package net.cacheux.nvplib.data

import net.cacheux.bytonio.BinaryDeserializer
import net.cacheux.bytonio.BinarySerializable
import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.annotations.Deserializer
import net.cacheux.bytonio.annotations.EncodeAsShort
import net.cacheux.bytonio.utils.ByteArrayReader
import net.cacheux.bytonio.utils.reader
import net.cacheux.nvplib.data.Apdu.Companion.AARE
import net.cacheux.nvplib.data.Apdu.Companion.AARQ
import net.cacheux.nvplib.data.Apdu.Companion.ABRT
import net.cacheux.nvplib.data.Apdu.Companion.PRST
import net.cacheux.nvplib.data.Apdu.Companion.RLRE
import net.cacheux.nvplib.data.Apdu.Companion.RLRQ
import net.cacheux.nvplib.generated.ApduSerializer

@DataObject
@Deserializer(ApduDeserializer::class)
data class Apdu(
    @EncodeAsShort val at: Int,
    val payload: BinarySerializable
): BinarySerializable {
    companion object {
        const val AARQ = 0xE200
        const val AARE = 0xE300
        const val RLRQ = 0xE400
        const val RLRE = 0xE500
        const val ABRT = 0xE600
        const val PRST = 0xE700
    }

    override fun getBinarySize() = ApduSerializer.getBinarySize(this)
    override fun toByteArray() = ApduSerializer.toByteArray(this)
}

object ApduDeserializer: BinaryDeserializer<Apdu> {
    override fun fromByteArray(byteArray: ByteArray) = fromByteArrayReader(byteArray.reader())

    override fun fromByteArrayReader(reader: ByteArrayReader): Apdu {
        val at = reader.readShort()
        reader.readShort()

        val payload = when (at) {
            AARQ, AARE -> ARequestDeserializer.fromByteArrayReader(reader)
            PRST -> DataApduDeserializer.fromByteArrayReader(reader)
            RLRQ, RLRE, ABRT -> DummyBinarySerializable()
            else -> throw IllegalStateException("Unknown at value $at")
        }

        return Apdu(at, payload)
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
