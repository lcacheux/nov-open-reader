package net.cacheux.nvp.app.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.cacheux.nvp.logging.logDebug
import net.cacheux.nvplib.data.PenResult
import net.cacheux.nvplib.nfc.desktop.NfcDriver
import net.cacheux.nvplib.noStopCondition
import net.cacheux.nvplib.utils.ByteArrayStore
import net.cacheux.nvplib.utils.dumpHex

class NfcPenInfoRepository(
    private val stopConditionProvider: StopConditionProvider = StopConditionProvider { noStopCondition },
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
): BasePenInfoRepository() {

    private val store = MutableStateFlow<ByteArrayStore?>(null)

    private var callbacks: PenInfoRepository.Callbacks? = null

    override fun registerCallbacks(callbacks: PenInfoRepository.Callbacks) {
        this.callbacks = callbacks
    }

    override fun getDataStore() = store

    private val byteArrayStore = ByteArrayStore()
    private val nfcController = NfcDriver(
        runner = {
            coroutineScope.launch { it() }
        }
    )

    init {
        nfcController.monitorNfc(
            onDataRead = { result ->
                logDebug { "onDataRead called" }
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
}
