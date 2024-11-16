package net.cacheux.nvp.app

import kotlinx.coroutines.flow.StateFlow
import net.cacheux.nvp.model.Dose
import net.cacheux.nvplib.data.PenResult
import net.cacheux.nvplib.utils.ByteArrayStore

interface PenInfoRepository {
    fun getDoseList(): StateFlow<List<Dose>>
    fun getDataStore(): StateFlow<ByteArrayStore?>

    fun isReading(): StateFlow<Boolean>
    fun getReadMessage(): StateFlow<String?>
    fun clearReadMessage()

    fun registerOnDataReceivedCallback(callback: (result: PenResult) -> Unit)
}
