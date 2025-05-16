package net.cacheux.nvp.app

import kotlinx.coroutines.flow.MutableStateFlow
import net.cacheux.nvp.app.repository.PenInfoRepository
import net.cacheux.nvp.model.Dose
import net.cacheux.nvplib.data.PenResult

class TestingPenInfoRepository: PenInfoRepository {

    private var callbacks: PenInfoRepository.Callbacks? = null

    override fun getDoseList() = MutableStateFlow<List<Dose>>(listOf())

    override fun getDataStore() = MutableStateFlow(null)

    override fun registerOnDataReceivedCallback(callback: (result: PenResult) -> Unit) {
        // Ignore
    }

    override fun registerCallbacks(callbacks: PenInfoRepository.Callbacks) {
        this.callbacks = callbacks
    }

    fun readStart() {
        callbacks?.onReadStart?.invoke()
    }

    fun readStop() {
        callbacks?.onReadStop?.invoke()
    }

    fun readError(e: Exception) {
        callbacks?.onError?.invoke(e)
    }
}
