package net.cacheux.nvp.app

import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import net.cacheux.nvp.app.utils.csvToDoseList
import net.cacheux.nvp.logging.logDebug
import net.cacheux.nvp.model.Dose
import net.cacheux.nvp.model.DoseGroup
import net.cacheux.nvp.model.IoB
import net.cacheux.nvplib.NvpController
import net.cacheux.nvplib.testing.TestingDataReader
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

class MainScreenViewModel(
    private val repository: PenInfoRepository,
    private val storageRepository: StorageRepository,
    private val doseListUseCase: DoseListUseCase,
    private val iobUseCase: IoBUseCase,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    fun getPenList() = storageRepository.getPenList()

    private val isReading = MutableStateFlow(false)
    fun isReading(): StateFlow<Boolean> = isReading

    private val readMessage = MutableStateFlow<String?>(null)
    fun getReadMessage(): StateFlow<String?> = readMessage

    fun clearPopup() {
        isReading.value = false
        readMessage.value = null
    }

    private val currentPen = MutableStateFlow<String?>(null)
    fun getCurrentPen(): StateFlow<String?> = currentPen

    fun setCurrentPen(serial: String?) {
        logDebug { "setCurrentPen $serial"}
        currentPen.value = serial
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val doseList: Flow<List<DoseGroup>> = currentPen.flatMapLatest {
        doseListUseCase.getDoseGroups(it)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val flatDoseList: Flow<List<Dose>> = currentPen.flatMapLatest {
        storageRepository.getDoseList(it)
    }

    val iob: Flow<IoB?> = iobUseCase.calculate(doseList, flow {
        while(true) {
            emit(System.currentTimeMillis())
            delay(TimeUnit.MINUTES.toMillis(1))
        }
    })

    val store = repository.getDataStore()

    fun loadCsvFile(file: PlatformFile) {
        coroutineScope.launch {
            file.readBytes().toString(Charset.defaultCharset()).csvToDoseList().let { doseList ->
                if (doseList.isEmpty()) {
                    readMessage.value = "No data found in CSV file"
                } else {
                    readMessage.value = "Loading CSV..."
                    isReading.value = true
                    storageRepository.saveDoseList(doseList)
                    readMessage.value = "CSV loaded"
                    isReading.value = false
                }
            }
        }
    }

    fun loadFromFile(file: PlatformFile) {
        coroutineScope.launch {
            logDebug { "loadFromFile" }

            val dataReader = TestingDataReader(file.readBytes().inputStream())
            val result = NvpController(dataReader).dataRead()

            isReading.value = true
            storageRepository.saveResult(result)
            isReading.value = false
        }
    }
}
