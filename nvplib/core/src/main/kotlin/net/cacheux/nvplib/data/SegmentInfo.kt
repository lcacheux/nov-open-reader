package net.cacheux.nvplib.data

import net.cacheux.nvplib.data.Attribute.Companion.ATTR_PM_SEG_MAP
import net.cacheux.nvplib.data.Attribute.Companion.ATTR_SEG_USAGE_CNT
import net.cacheux.nvplib.utils.getUnsignedShort
import net.cacheux.nvplib.utils.wrap
import java.nio.ByteBuffer

data class SegmentInfo(
    val instnum: Int,
    val usage: Int = -1,
    val items: List<Attribute>,
    val segmentInfoMap: SegmentInfoMap? = null,
) {
    companion object {
        fun fromByteBuffer(buffer: ByteBuffer): SegmentInfo {
            val instnum = buffer.getUnsignedShort()
            val count = buffer.getUnsignedShort()
            buffer.getUnsignedShort() // length
            var usage = -1
            var segmentInfoMap: SegmentInfoMap? = null
            val items = mutableListOf<Attribute>()
            repeat(count) {
                val attribute = Attribute.fromByteBuffer(buffer)
                when (attribute.type) {
                    ATTR_PM_SEG_MAP -> segmentInfoMap = SegmentInfoMap.fromByteBuffer(attribute.data.wrap())
                    ATTR_SEG_USAGE_CNT -> usage = attribute.value
                }
                items.add(attribute)
            }

            return SegmentInfo(instnum, usage, items, segmentInfoMap)
        }
    }
}
