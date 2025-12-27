package net.cacheux.nvplib.nfc.desktop

import com.ingenico.android.common.nfc.smartcardio.Terminal
import net.cacheux.nvplib.NvpController
import net.cacheux.nvplib.StopCondition
import net.cacheux.nvplib.data.PenResult
import net.cacheux.nvplib.noStopCondition

class NfcDriver(
    private val runner: (() -> Unit) -> Unit = { it() }
) {
    fun monitorNfc(
        onDataRead: (PenResult) -> Unit,
        onTagDetected: () -> Unit = {},
        onDataSent: (data: ByteArray) -> Unit = {},
        onDataReceived: (data: ByteArray) -> Unit = {},
        onError: (Exception) -> Unit = {},
        stopCondition: StopCondition = noStopCondition
    ) {
        runner {
            println("monitorNfc in NfcDriver")
            try {
                val terminal = Terminal.getFirstTerminal()

                println("terminal: ${terminal.name}")

                do {
                    terminal.waitForCard()

                    onTagDetected()

                    val channel = terminal.connect()

                    // Override the NfcDataReader to call onDataSent / onDataReceived
                    val dataReader = object : NfcDesktopReader(channel) {
                        override fun onDataSent(data: ByteArray) = onDataSent(data)
                        override fun onDataReceived(data: ByteArray) = onDataReceived(data)
                    }

                    val controller = NvpController(dataReader)

                    val result = controller.dataRead(stopCondition)
                    onDataRead(result)

                    terminal.waitForEnd()
                    //channel.close()
                } while (true)
            } catch (e: Exception) {
                e.printStackTrace()
                onError(e)
            }
        }
    }
}
