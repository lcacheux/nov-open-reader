package net.cacheux.nvplib.data

import net.cacheux.nvplib.annotations.IsInt
import net.cacheux.nvplib.utils.getUnsignedInt
import java.nio.ByteBuffer

data class InsulinDose(
    @IsInt val time: Long,
    @IsInt val units: Int,
    @IsInt val flags: Int
): Encodable() {
    companion object {
        fun fromByteBuffer(buffer: ByteBuffer): InsulinDose {
            val relativeTime = buffer.getUnsignedInt()
            val units = (buffer.getUnsignedInt() and 0xFFFF)
            val flags = buffer.getUnsignedInt()

            return InsulinDose(relativeTime.toLong(), units, flags)
        }
    }

    /**
     * Convert an InsulinDose to set time to the current time in millis instead of a relative time
     * from the beginning of the pen.
     * @param relativeTime Should be the value parsed with [EventReport]
     * @param currentTime Current time in millis. Should be System.currentTimeMillis()
     */
    fun withUtcTime(relativeTime: Int, currentTime: Long = System.currentTimeMillis()) = InsulinDose(
        time = (currentTime - ( (relativeTime - time) * 1000 )),
        units = units, flags = flags
    )
}
