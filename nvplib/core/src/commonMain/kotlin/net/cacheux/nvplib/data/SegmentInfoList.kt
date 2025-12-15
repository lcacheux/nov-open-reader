package net.cacheux.nvplib.data

import net.cacheux.bytonio.BinarySerializable
import net.cacheux.bytonio.utils.ByteArrayReader
import net.cacheux.bytonio.utils.byteArrayOf

data class SegmentInfoList(
    val items: List<SegmentInfo> = listOf()
): BinarySerializable {
    override fun getBinarySize() = 0
    override fun toByteArray() = byteArrayOf()

    companion object {
        fun fromByteArrayReader(reader: ByteArrayReader): SegmentInfoList {
            val count = reader.readShort()
            reader.readShort() // length

            return SegmentInfoList(
                (1..count).map {
                    SegmentInfo.fromByteArrayReader(reader)
                }
            )
        }
    }
}
