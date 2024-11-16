package net.cacheux.nvplib.data

import net.cacheux.nvplib.annotations.IsShort
import java.nio.ByteBuffer

data class ConfirmedAction private constructor(
    @IsShort val handle: Int,
    @IsShort val type: Int,
    val bytes: ByteArray
): Encodable() {
    companion object {
        const val STORE_HANDLE = 0x100
        const val ALL_SEGMENTS = 0x0001

        fun allSegment(handle: Int, type: Int) = ConfirmedAction(
            handle = handle, type = type,
            bytes = byteArrayOf(0, 1, 0, 2, 0, 0)
        )

        fun segment(handle: Int, type: Int, segment: Int) = ConfirmedAction(
            handle = handle, type = type,
            bytes = ByteBuffer.allocate(2).putShort(segment.toShort()).array()
        )
    }
}
