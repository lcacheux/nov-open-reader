package net.cacheux.nvplib.data

import net.cacheux.bytonio.utils.ByteArrayReader
import net.cacheux.bytonio.utils.reader
import net.cacheux.nvplib.data.Attribute.Companion.ATTR_PM_SEG_MAP
import net.cacheux.nvplib.data.Attribute.Companion.ATTR_SEG_USAGE_CNT

data class SegmentInfo(
    val instnum: Int,
    val usage: Int = -1,
    val items: List<Attribute>,
    val segmentInfoMap: SegmentInfoMap? = null,
) {
    companion object {
        fun fromByteArrayReader(reader: ByteArrayReader): SegmentInfo {
            val instnum = reader.readShort()
            val count = reader.readShort()
            reader.readShort() // length
            var usage = -1
            var segmentInfoMap: SegmentInfoMap? = null
            val items = mutableListOf<Attribute>()
            repeat(count) {
                val attribute = AttributeDeserializer.fromByteArrayReader(reader)
                when (attribute.type) {
                    ATTR_PM_SEG_MAP -> segmentInfoMap = SegmentInfoMap.fromByteArrayReader(attribute.data.reader())
                    ATTR_SEG_USAGE_CNT -> usage = attribute.value
                }
                items.add(attribute)
            }

            return SegmentInfo(instnum, usage, items, segmentInfoMap)
        }
    }
}
