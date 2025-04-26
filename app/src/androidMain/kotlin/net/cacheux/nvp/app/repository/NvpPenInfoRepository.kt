package net.cacheux.nvp.app.repository

import android.app.Activity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
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

    private var callbacks: PenInfoRepository.Callbacks? = null

    override fun registerCallbacks(callbacks: PenInfoRepository.Callbacks) {
        this.callbacks = callbacks
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
                        callbacks?.onReadStop?.invoke()
                        if (result is PenResult.Success) {
                            setResult(result)
                            store.value = byteArrayStore
                        }
                    },
                    onTagDetected = {
                        callbacks?.onReadStart?.invoke()
                        store.value = null
                        byteArrayStore.clear()
                    },
                    onDataSent = { logDebug { "Data sent >>>>>>>> ${it.dumpHex()}" } },
                    onDataReceived = {
                        logDebug { "Data received <<<<<<<< ${it.dumpHex()}" }
                        byteArrayStore.addByteArray(it)
                    },
                    onError = { e ->
                        callbacks?.onError?.invoke(e)
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
