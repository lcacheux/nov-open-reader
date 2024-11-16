package net.cacheux.nvplib.data

import net.cacheux.nvplib.annotations.IsShort
import net.cacheux.nvplib.utils.getUnsignedShort
import java.nio.ByteBuffer

data class TriggerSegmentDataXfer(
    @IsShort val segmentId: Int,
    @IsShort val responseCode: Int,
): Encodable() {
    companion object {
        fun fromByteBuffer(buffer: ByteBuffer): TriggerSegmentDataXfer {
            val segment = buffer.getUnsignedShort()
            val response = buffer.getUnsignedShort()

            return TriggerSegmentDataXfer(segment, response)
        }
    }

    fun isOkay() = segmentId != 0 && responseCode == 0
}
