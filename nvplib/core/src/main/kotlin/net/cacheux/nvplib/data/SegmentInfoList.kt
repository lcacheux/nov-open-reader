package net.cacheux.nvplib.data

import net.cacheux.nvplib.utils.getUnsignedShort
import java.nio.ByteBuffer

data class SegmentInfoList(
    val items: List<SegmentInfo> = listOf()
): Encodable() {
    companion object {
        fun fromByteBuffer(buffer: ByteBuffer): SegmentInfoList {
            val count = buffer.getUnsignedShort()
            buffer.getUnsignedShort() // length

            return SegmentInfoList(
                (1..count).map {
                    SegmentInfo.fromByteBuffer(buffer)
                }
            )
        }
    }
}
