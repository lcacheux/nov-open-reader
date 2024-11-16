package net.cacheux.nvplib.data

import net.cacheux.nvplib.annotations.IsShort
import net.cacheux.nvplib.utils.getByteArray
import net.cacheux.nvplib.utils.getUnsignedShort
import java.nio.ByteBuffer

data class SegmentEntry(
    @IsShort val classId: Int,
    @IsShort val metricType: Int,
    @IsShort val otype: Int,
    @IsShort val handle: Int,
    @IsShort val amCount: Int,
    val data: ByteArray
) {
    companion object {
        fun fromByteBuffer(buffer: ByteBuffer): SegmentEntry = SegmentEntry(
            classId = buffer.getUnsignedShort(),
            metricType = buffer.getUnsignedShort(),
            otype = buffer.getUnsignedShort(),
            handle = buffer.getUnsignedShort(),
            amCount = buffer.getUnsignedShort(),
            data = buffer.getByteArray()
        )
    }
}
