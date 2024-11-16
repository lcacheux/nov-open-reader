package net.cacheux.nvp.app

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import net.cacheux.nvp.logging.logDebug
import net.cacheux.nvp.model.Dose
import net.cacheux.nvplib.NvpController
import net.cacheux.nvplib.testing.TestingDataReader
import java.io.InputStream

class MainScreenViewModel(
    private val repository: PenInfoRepository,
    private val storageRepository: StorageRepository,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    fun getPenList() = storageRepository.getPenList()

    private val currentPen = MutableStateFlow<String?>(null)
    fun getCurrentPen(): StateFlow<String?> = currentPen

    fun setCurrentPen(serial: String?) {
        logDebug { "setCurrentPen $serial"}
        currentPen.value = serial
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val doseList: Flow<List<Dose>> = currentPen.flatMapLatest {
        storageRepository.getDoseList(it)
    }

    val store = repository.getDataStore()

    fun loadFromFile(inputStream: InputStream) {
        logDebug { "loadFromFile" }

        val dataReader = TestingDataReader(inputStream)
        val result = NvpController(dataReader).dataRead()
        coroutineScope.launch {
            storageRepository.saveResult(result)
        }

        /*if (repository is LoadablePenInfoRepository) {
            repository.loadData(dataReader)
        }*/
    }
}