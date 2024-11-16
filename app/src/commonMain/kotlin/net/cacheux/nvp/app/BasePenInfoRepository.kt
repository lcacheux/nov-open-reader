package net.cacheux.nvp.app

import kotlinx.coroutines.flow.MutableStateFlow
import net.cacheux.nvp.model.Dose
import net.cacheux.nvplib.DataReader
import net.cacheux.nvplib.NvpController
import net.cacheux.nvplib.data.PenResult

abstract class BasePenInfoRepository: LoadablePenInfoRepository {
    private val doseList = MutableStateFlow<List<Dose>>(listOf())

    private var callback: (result: PenResult) -> Unit = {}

    override fun getDoseList() = doseList

    override fun registerOnDataReceivedCallback(callback: (result: PenResult) -> Unit) {
        this.callback = callback
    }

    protected fun setResult(result: PenResult) {
        callback(result)
        if (result is PenResult.Success) {
            doseList.value = result.data.doseList.map { dose ->
                Dose(
                    dose.time,
                    dose.units,
                    serial = result.data.serial
                )
            }
        }
    }

    override fun loadData(dataReader: DataReader) {
        setResult(NvpController(dataReader).dataRead())
    }
}
