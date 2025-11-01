package net.cacheux.nvplib.data

import net.cacheux.bytonio.BinarySerializable
import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.annotations.EncodeAsShort
import net.cacheux.nvplib.generated.TriggerSegmentDataXferSerializer

@DataObject
data class TriggerSegmentDataXfer(
    @EncodeAsShort val segmentId: Int,
    @EncodeAsShort val responseCode: Int,
): BinarySerializable {
    fun isOkay() = segmentId != 0 && responseCode == 0
    override fun getBinarySize() = TriggerSegmentDataXferSerializer.getBinarySize(this)
    override fun toByteArray() = TriggerSegmentDataXferSerializer.toByteArray(this)
}
