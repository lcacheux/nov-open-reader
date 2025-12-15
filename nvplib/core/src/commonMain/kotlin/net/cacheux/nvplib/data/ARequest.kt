package net.cacheux.nvplib.data

import net.cacheux.bytonio.BinaryDeserializer
import net.cacheux.bytonio.BinarySerializable
import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.annotations.Deserializer
import net.cacheux.bytonio.annotations.EncodeAsShort
import net.cacheux.bytonio.utils.ByteArrayReader
import net.cacheux.bytonio.utils.reader
import net.cacheux.nvplib.data.ApoepElement.Companion.APOEP
import net.cacheux.nvplib.generated.ARequestSerializer
import net.cacheux.nvplib.generated.ApoepElementDeserializer

@DataObject
@Deserializer(ARequestDeserializer::class)
data class ARequest(
    val protocol: Int,
    val version: Int,
    @EncodeAsShort val elements: Int,
    val apoep: ApoepElement
): BinarySerializable {
    override fun getBinarySize() = ARequestSerializer.getBinarySize(this)
    override fun toByteArray() = ARequestSerializer.toByteArray(this)
}

object ARequestDeserializer: BinaryDeserializer<ARequest> {
    override fun fromByteArray(byteArray: ByteArray) =
        fromByteArrayReader(byteArray.reader())

    override fun fromByteArrayReader(reader: ByteArrayReader): ARequest {
        val version = reader.readInt()
        val elements = reader.readShort()
        reader.readShort() // len

        var apoep: ApoepElement? = null
        repeat(elements) {
            val protocol = reader.readShort()
            val bytes = reader.readByteArray(reader.readShort())
            if (protocol == APOEP) {
                apoep = ApoepElementDeserializer.fromByteArray(bytes)
            }
        }

        apoep?.let {
            return ARequest(protocol = APOEP, version, elements, it)
        } ?: throw IllegalStateException("APOEP packet not found")
    }
}
