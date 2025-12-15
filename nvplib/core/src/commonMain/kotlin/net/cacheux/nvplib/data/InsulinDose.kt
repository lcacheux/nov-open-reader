package net.cacheux.nvplib.data

import net.cacheux.bytonio.BinaryDeserializer
import net.cacheux.bytonio.BinarySerializable
import net.cacheux.bytonio.annotations.DataObject
import net.cacheux.bytonio.annotations.Deserializer
import net.cacheux.bytonio.annotations.EncodeAsInt
import net.cacheux.bytonio.utils.ByteArrayReader
import net.cacheux.bytonio.utils.reader
import net.cacheux.nvplib.generated.InsulinDoseSerializer
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@DataObject
@Deserializer(InsulinDoseDeserializer::class)
data class InsulinDose(
    @EncodeAsInt val time: Long,
    @EncodeAsInt val units: Int,
    @EncodeAsInt val flags: Int
): BinarySerializable {
    companion object {
        const val VALID_FLAG = 0x08000000
    }

    /**
     * Convert an InsulinDose to set time to the current time in millis instead of a relative time
     * from the beginning of the pen.
     * @param relativeTime Should be the value parsed with [EventReport]
     * @param currentTime Current time in millis. Should be System.currentTimeMillis()
     */
    fun withUtcTime(relativeTime: Int, currentTime: Long = Clock.System.now().toEpochMilliseconds()) = InsulinDose(
        time = (currentTime - ( (relativeTime - time) * 1000 )),
        units = units, flags = flags
    )

    override fun getBinarySize() = InsulinDoseSerializer.getBinarySize(this)
    override fun toByteArray() = InsulinDoseSerializer.toByteArray(this)
}

object InsulinDoseDeserializer: BinaryDeserializer<InsulinDose> {
    override fun fromByteArray(byteArray: ByteArray) = fromByteArrayReader(byteArray.reader())

    override fun fromByteArrayReader(reader: ByteArrayReader): InsulinDose {
        val relativeTime = reader.readInt()
        val units = (reader.readInt() and 0xFFFF)
        val flags = reader.readInt()

        return InsulinDose(relativeTime.toLong(), units, flags)
    }
}
