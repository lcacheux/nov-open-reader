package net.cacheux.nvplib.nfc.desktop

import com.ingenico.android.common.nfc.smartcardio.Channel
import net.cacheux.nvplib.DataReader
import java.nio.ByteBuffer

open class NfcDesktopReader(
    private val channel: Channel
): DataReader {
    override fun readData(input: ByteArray): ByteArray {
        val output = ByteBuffer.allocate(1024)
        val size = channel.transmit(ByteBuffer.wrap(input), output)

        return output.array().copyOfRange(0, size)
    }
}