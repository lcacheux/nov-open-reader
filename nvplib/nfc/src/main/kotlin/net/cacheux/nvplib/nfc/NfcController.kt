package net.cacheux.nvplib.nfc

import android.app.Activity
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import net.cacheux.nvplib.NvpController
import net.cacheux.nvplib.StopCondition
import net.cacheux.nvplib.data.PenResult
import net.cacheux.nvplib.noStopCondition

/**
 * Base class to enable NFC reading.
 *
 * @param activity NFC reading must always be attached to an Android activity.
 * @param runner Optional parameter to specify a callback that will run the NFC reading. ie this
 *               could be used to run into coroutines or other threading methods.
 */
class NfcController(
    private val activity: Activity,
    private val runner: (() -> Unit) -> Unit = { it() }
) {
    private val nfcAdapter = NfcAdapter.getDefaultAdapter(activity)

    /**
     * Start monitoring the NFC reader to detect pen tags. The parameters for this method are
     * callbacks that will be called on different events.
     *
     * @param onDataRead Callback once the reading is over, either with success or failure
     * @param onTagDetected Callback when a pen tag has been detected
     * @param onDataSent Callback for each data packet sent to the pen
     * @param onDataReceived Callback for each data packet received from the pen
     * @param onError Callback when any NFC reading exception occurred
     * @param stopCondition A method called each time a list of doses is retrieved, that can return
     *                      true to end the pen reading.
     */
    fun monitorNfc(
        onDataRead: (PenResult) -> Unit,
        onTagDetected: (tag: Tag) -> Unit = {},
        onDataSent: (data: ByteArray) -> Unit = {},
        onDataReceived: (data: ByteArray) -> Unit = {},
        onError: (Exception) -> Unit = {},
        stopCondition: StopCondition = noStopCondition
    ) {
        nfcAdapter?.enableReaderMode(
            activity,
            { tag ->
                runner {
                    onTagDetected(tag)
                    try {
                        val isoDep = IsoDep.get(tag)
                        isoDep.connect()

                        // Override the NfcDataReader to call onDataSent / onDataReceived
                        val dataReader = object : NfcDataReader(isoDep) {
                            override fun onDataSent(data: ByteArray) = onDataSent(data)
                            override fun onDataReceived(data: ByteArray) = onDataReceived(data)
                        }

                        val controller = NvpController(dataReader)

                        isoDep.timeout = 1000

                        val result = controller.dataRead(stopCondition)
                        onDataRead(result)

                        isoDep.close()
                    } catch (e: Exception) {
                        onError(e)
                    }
                }
            },
            NfcAdapter.FLAG_READER_NFC_A or NfcAdapter.FLAG_READER_NFC_V or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
            null
        )
    }

    /**
     * Stop monitoring NFC reader.
     */
    fun stopNfc() {
        nfcAdapter?.disableReaderMode(activity)
    }
}
