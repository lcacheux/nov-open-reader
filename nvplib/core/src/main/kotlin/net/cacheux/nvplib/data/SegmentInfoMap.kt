package net.cacheux.nvplib.data

import net.cacheux.nvplib.annotations.IsShort
import net.cacheux.nvplib.utils.getUnsignedShort
import java.nio.ByteBuffer

data class SegmentInfoMap(
    @IsShort val bits: Int,
    @IsShort val count: Int,
    @IsShort val length: Int,
    val items: List<SegmentEntry>
) {
    companion object {
        fun fromByteBuffer(buffer: ByteBuffer): SegmentInfoMap {
            val bits = buffer.getUnsignedShort()
            val count = buffer.getUnsignedShort()
            val length = buffer.getUnsignedShort()
            val items = mutableListOf<SegmentEntry>()
            repeat(count) {
                items.add(SegmentEntry.fromByteBuffer(buffer))
            }

            return SegmentInfoMap(bits, count, length, items)
        }
    }
}
