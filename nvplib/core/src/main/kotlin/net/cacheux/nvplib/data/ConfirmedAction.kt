package net.cacheux.nvplib.data

import net.cacheux.bytonio.BinarySerializable
import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.annotations.EncodeAsShort
import net.cacheux.bytonio.utils.writer
import net.cacheux.nvplib.generated.ConfirmedActionSerializer

@DataObject
data class ConfirmedAction(
    @EncodeAsShort val handle: Int,
    @EncodeAsShort val type: Int,
    val bytes: ByteArray
): BinarySerializable {
    companion object {
        const val STORE_HANDLE = 0x100
        const val ALL_SEGMENTS = 0x0001

        fun allSegment(handle: Int, type: Int) = ConfirmedAction(
            handle = handle, type = type,
            bytes = byteArrayOf(0, 1, 0, 2, 0, 0)
        )

        fun segment(handle: Int, type: Int, segment: Int) = ConfirmedAction(
            handle = handle, type = type,
            bytes = ByteArray(2).writer().apply { writeShort(segment.toShort()) }.byteArray
        )
    }

    override fun getBinarySize() = ConfirmedActionSerializer.getBinarySize(this)
    override fun toByteArray() = ConfirmedActionSerializer.toByteArray(this)
}
