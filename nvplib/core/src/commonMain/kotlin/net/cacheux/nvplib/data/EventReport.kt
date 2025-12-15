package net.cacheux.nvplib.data

import net.cacheux.bytonio.annotations.IgnoreEncoding
import net.cacheux.bytonio.utils.ByteArrayReader
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class EventReport(
    val handle: Int,
    val relativeTime: Int,
    val eventType: Int,
    val configuration: Configuration? = null,
    val instance: Int = -1,
    val index: Int = -1,
    @IgnoreEncoding val insulinDoses: List<InsulinDose> = listOf()
): DummyBinarySerializable() {
    companion object {
        const val MDC_NOTI_CONFIG: Int = 3356
        const val MDC_NOTI_SEGMENT_DATA: Int = 3361

        fun fromByteArrayReader(reader: ByteArrayReader): EventReport {
            val handle = reader.readShort()
            val relativeTime = reader.readInt()
            val eventType = reader.readShort()
            reader.readShort() // len

            val doses = mutableListOf<InsulinDose>()

            return when (eventType) {
                MDC_NOTI_SEGMENT_DATA -> {
                    val instance = reader.readShort()
                    val index = reader.readInt()
                    val count = reader.readInt()
                    reader.readShort() // status
                    reader.readShort() // bcount

                    val currentTime = Clock.System.now().toEpochMilliseconds()

                    repeat(count) {
                        InsulinDoseDeserializer.fromByteArrayReader(reader).let {
                            if (it.flags == InsulinDose.VALID_FLAG)
                                doses.add(it.withUtcTime(relativeTime, currentTime))
                        }
                    }
                    EventReport(handle, relativeTime, eventType, null, instance, index, doses)
                }
                MDC_NOTI_CONFIG -> {
                    EventReport(handle, relativeTime, eventType, Configuration.fromByteArrayReader(reader))
                }
                else -> EventReport(handle, relativeTime, eventType)
            }
        }
    }
}
