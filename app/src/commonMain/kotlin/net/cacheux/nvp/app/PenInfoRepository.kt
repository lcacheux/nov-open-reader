package net.cacheux.nvp.app

import kotlinx.coroutines.flow.StateFlow
import net.cacheux.nvp.model.Dose
import net.cacheux.nvplib.data.PenResult
import net.cacheux.nvplib.utils.ByteArrayStore

interface PenInfoRepository {
    fun getDoseList(): StateFlow<List<Dose>>
    fun getDataStore(): StateFlow<ByteArrayStore?>

    fun registerOnDataReceivedCallback(callback: (result: PenResult) -> Unit)
    fun registerCallbacks(callbacks: Callbacks)

    data class Callbacks(
        val onReadStart: () -> Unit,
        val onReadStop: () -> Unit,
        val onError: (e: Exception) -> Unit
    )
}
