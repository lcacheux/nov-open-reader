package net.cacheux.nvplib.data

import net.cacheux.bytonio.BinarySerializable
import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.annotations.EncodeAsShort
import net.cacheux.nvplib.generated.EventRequestSerializer

@DataObject
data class EventRequest(
    @EncodeAsShort val handle: Int,
    val currentTime: Int,
    @EncodeAsShort val type: Int,
    val data: ByteArray
): BinarySerializable {
    override fun getBinarySize() = EventRequestSerializer.getBinarySize(this)
    override fun toByteArray() = EventRequestSerializer.toByteArray(this)
}
