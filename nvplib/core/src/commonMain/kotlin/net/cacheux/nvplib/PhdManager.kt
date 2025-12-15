package net.cacheux.nvplib

import net.cacheux.bytonio.BinarySerializable
import net.cacheux.bytonio.utils.reader
import net.cacheux.bytonio.utils.writer
import net.cacheux.nvplib.data.Apdu
import net.cacheux.nvplib.data.ApduDeserializer
import net.cacheux.nvplib.data.DataApdu
import net.cacheux.nvplib.data.PhdPacket
import net.cacheux.nvplib.data.T4Update
import net.cacheux.nvplib.utils.decomposeNumber
import net.cacheux.nvplib.utils.getUnsignedShort

class PhdManager(
    private val dataReader: DataReader
) {
    companion object {
        const val MAX_READ_SIZE = 255
    }

    private var sequence = 1

    private fun request(data: ByteArray): TransceiveResult {
        return dataReader.readResult(data)
    }

    fun sendEmptyRequest(): ByteArray = sendRequest(byteArrayOf())

    private fun sendRequest(data: ByteArray): ByteArray {
        val phd = PhdPacket(
            seq = sequence++,
            content = data
        )
        val update = T4Update(phd.toByteArray())

        request(update.toByteArray())

        val readLen = request(createReadPayload(0, 2))
        val len = readLen.content.getUnsignedShort()

        val reads = decomposeNumber(len, MAX_READ_SIZE)

        val fullResult = ByteArray(len)
        val fullResultWriter = fullResult.writer()

        reads.forEachIndexed { index, i ->
            val readResult = request(createReadPayload(2 + index * MAX_READ_SIZE, i))
            fullResultWriter.writeByteArray(readResult.content.byteArray)
        }

        val resultPhd = PhdPacket.fromByteArrayReader(fullResult.reader())

        sequence = resultPhd.seq + 1

        val ack = T4Update(byteArrayOf(0xd0.toByte(), 0x00, 0x00))
        dataReader.readResult(ack.toByteArray())

        return resultPhd.content
    }

    fun sendApduRequest(apdu: Apdu) = sendRequest(apdu.toByteArray())

    fun <T: BinarySerializable> decodeDataApduRequest(inputApdu: Apdu): T {
        val byteArray = sendApduRequest(inputApdu)
        val outputApdu = ApduDeserializer.fromByteArray(byteArray)
        (outputApdu.payload as DataApdu).let { dataApdu ->
            return dataApdu.payload as T
        }
    }

    fun <T: BinarySerializable> decodeRequest(input: ByteArray): T {
        val output = sendRequest(input)
        val apdu = ApduDeserializer.fromByteArray(output)
        (apdu.payload as DataApdu).let { dataApdu ->
            return dataApdu.payload as T
        }
    }
}
