package net.cacheux.nvp.app.repository

import android.app.Activity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import net.cacheux.nvp.app.BasePenInfoRepository
import net.cacheux.nvp.app.StopConditionProvider
import net.cacheux.nvp.logging.logDebug
import net.cacheux.nvplib.data.PenResult
import net.cacheux.nvplib.nfc.NfcController
import net.cacheux.nvplib.noStopCondition
import net.cacheux.nvplib.utils.ByteArrayStore
import net.cacheux.nvplib.utils.dumpHex
import javax.inject.Inject

class NvpPenInfoRepository @Inject constructor(
    private val stopConditionProvider: StopConditionProvider = StopConditionProvider { noStopCondition }
): BasePenInfoRepository(), ActivityRequirer {
    private val store = MutableStateFlow<ByteArrayStore?>(null)

    private val isReading = MutableStateFlow(false)
    override fun isReading() = isReading

    private val readMessage = MutableStateFlow<String?>(null)
    override fun getReadMessage() = readMessage

    override fun clearReadMessage() {
        readMessage.value = null
    }

    override fun getDataStore() = store

    private val byteArrayStore = ByteArrayStore()
    private var nfcController: NfcController? = null
    override fun setActivity(activity: Activity?) {
        activity?.let {
            nfcController = NfcController(
                activity = activity,
                /*runner = {
                    coroutineScope.launch { it() }
                }*/
            ).apply {
                monitorNfc(
                    onDataRead = { result ->
                        isReading.value = false
                        readMessage.value = null
                        if (result is PenResult.Success) {
                            setResult(result)
                            store.value = byteArrayStore
                        }
                    },
                    onTagDetected = {
                        isReading.value = true
                        readMessage.value = "Reading pen"
                        store.value = null
                        byteArrayStore.clear()
                    },
                    onDataSent = { logDebug { "Data sent >>>>>>>> ${it.dumpHex()}" } },
                    onDataReceived = {
                        logDebug { "Data received <<<<<<<< ${it.dumpHex()}" }
                        byteArrayStore.addByteArray(it)
                    },
                    onError = { e ->
                        isReading.value = false
                        readMessage.value = e.message
                    },
                    stopCondition = { serial, list ->
                        runBlocking {
                            stopConditionProvider.getStopCondition()(serial, list)
                        }
                    }
                )
            }
        } ?: {
            nfcController?.stopNfc()
        }
    }
}
