package net.cacheux.nvplib.data

import net.cacheux.bytonio.utils.ByteArrayReader
import net.cacheux.nvplib.generated.SegmentEntryDeserializer

data class SegmentInfoMap(
    val bits: Int,
    val count: Int,
    val length: Int,
    val items: List<SegmentEntry>
) {
    companion object {
        fun fromByteArrayReader(reader: ByteArrayReader): SegmentInfoMap {
            val bits = reader.readShort()
            val count = reader.readShort()
            val length = reader.readShort()
            val items = mutableListOf<SegmentEntry>()
            repeat(count) {
                items.add(SegmentEntryDeserializer.fromByteArrayReader(reader))
            }

            return SegmentInfoMap(bits, count, length, items)
        }
    }
}
