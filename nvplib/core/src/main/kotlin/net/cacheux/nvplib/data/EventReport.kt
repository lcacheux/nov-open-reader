package net.cacheux.nvplib.data

import net.cacheux.nvplib.annotations.IsInt
import net.cacheux.nvplib.annotations.IsShort
import net.cacheux.nvplib.utils.getUnsignedInt
import net.cacheux.nvplib.utils.getUnsignedShort
import java.nio.ByteBuffer

data class EventReport(
    @IsShort val handle: Int,
    @IsInt val relativeTime: Int,
    @IsShort val eventType: Int,
    val configuration: Configuration? = null,
    val instance: Int = -1,
    val index: Int = -1,
    val insulinDoses: List<InsulinDose> = listOf()
): Encodable() {
    companion object {
        const val MDC_NOTI_CONFIG: Int = 3356
        const val MDC_NOTI_SEGMENT_DATA: Int = 3361

        fun fromByteBuffer(buffer: ByteBuffer): EventReport {
            val handle = buffer.getUnsignedShort()
            val relativeTime = buffer.getUnsignedInt()
            val eventType = buffer.getUnsignedShort()
            buffer.getUnsignedShort() // len

            val doses = mutableListOf<InsulinDose>()

            return when (eventType) {
                MDC_NOTI_SEGMENT_DATA -> {
                    val instance = buffer.getUnsignedShort()
                    val index = buffer.getUnsignedInt()
                    val count = buffer.getUnsignedInt()
                    buffer.getUnsignedShort() // status
                    buffer.getUnsignedShort() // bcount

                    val currentTime = System.currentTimeMillis()

                    repeat(count) {
                        doses.add(InsulinDose
                            .fromByteBuffer(buffer)
                            .withUtcTime(relativeTime, currentTime)
                        )
                    }
                    EventReport(handle, relativeTime, eventType, null, instance, index, doses)
                }
                MDC_NOTI_CONFIG -> {
                    EventReport(handle, relativeTime, eventType, Configuration.fromByteBuffer(buffer))
                }
                else -> EventReport(handle, relativeTime, eventType)
            }
        }
    }
}
