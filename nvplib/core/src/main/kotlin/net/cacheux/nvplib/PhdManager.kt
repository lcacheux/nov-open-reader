package net.cacheux.nvplib

import net.cacheux.nvplib.data.Apdu
import net.cacheux.nvplib.data.DataApdu
import net.cacheux.nvplib.data.Encodable
import net.cacheux.nvplib.data.PhdPacket
import net.cacheux.nvplib.data.T4Update
import net.cacheux.nvplib.utils.decomposeNumber
import net.cacheux.nvplib.utils.getUnsignedShort
import net.cacheux.nvplib.utils.wrap
import java.nio.ByteBuffer

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

        val fullResult = ByteBuffer.allocate(len)

        reads.forEachIndexed { index, i ->
            val readResult = request(createReadPayload(2 + index * MAX_READ_SIZE, i))
            fullResult.put(readResult.content)
        }

        fullResult.rewind()

        val resultPhd = PhdPacket.fromByteBuffer(fullResult)

        sequence = resultPhd.seq + 1

        val ack = T4Update(byteArrayOf(0xd0.toByte(), 0x00, 0x00))
        dataReader.readResult(ack.toByteArray())

        return resultPhd.content
    }

    fun sendApduRequest(apdu: Apdu) = sendRequest(apdu.toByteArray())

    fun <T: Encodable> decodeDataApduRequest(inputApdu: Apdu): T {
        val byteArray = sendApduRequest(inputApdu)
        val outputApdu = Apdu.fromByteBuffer(byteArray.wrap())
        (outputApdu.payload as DataApdu).let { dataApdu ->
            return dataApdu.payload as T
        }
    }

    fun <T: Encodable> decodeRequest(input: ByteArray): T {
        val output = sendRequest(input)
        val apdu = Apdu.fromByteBuffer(output.wrap())
        (apdu.payload as DataApdu).let { dataApdu ->
            return dataApdu.payload as T
        }
    }
}
