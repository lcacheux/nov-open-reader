package net.cacheux.nvp.app.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import net.cacheux.nvp.app.repository.PenInfoRepository
import net.cacheux.nvp.app.repository.StorageRepository
import net.cacheux.nvp.app.usecase.DoseListUseCase
import net.cacheux.nvp.app.utils.csvToDoseList
import net.cacheux.nvp.logging.logDebug
import net.cacheux.nvp.model.Dose
import net.cacheux.nvp.model.DoseGroup
import net.cacheux.nvp.model.generateDoseData
import net.cacheux.nvp.ui.ui.generated.resources.Res
import net.cacheux.nvp.ui.ui.generated.resources.csv_loaded
import net.cacheux.nvp.ui.ui.generated.resources.loading_csv
import net.cacheux.nvp.ui.ui.generated.resources.no_csv_data
import org.jetbrains.compose.resources.StringResource
import java.io.InputStream

open class BaseMainScreenViewModel (
    private val repository: PenInfoRepository,
    private val storageRepository: StorageRepository,
    private val doseListUseCase: DoseListUseCase,
    protected val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
): ViewModel() {
    fun getPenList() = storageRepository.getPenList()

    protected val isReading = MutableStateFlow(false)
    fun isReading(): StateFlow<Boolean> = isReading

    protected val readMessage = MutableStateFlow<StringResource?>(null)
    fun getReadMessage(): StateFlow<StringResource?> = readMessage

    fun clearPopup() {
        isReading.value = false
        readMessage.value = null
    }

    private val currentPen = MutableStateFlow<String?>(null)
    fun getCurrentPen(): StateFlow<String?> = currentPen

    fun setCurrentPen(serial: String?) {
        logDebug { "setCurrentPen $serial" }
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

    val store = repository.getDataStore()

    fun loadCsvFile(input: InputStream) {
        input.reader().use {
            it.readText().csvToDoseList().let { doseList ->
                if (doseList.isEmpty()) {
                    readMessage.value = Res.string.no_csv_data
                } else {
                    coroutineScope.launch {
                        readMessage.value = Res.string.loading_csv
                        isReading.value = true
                        storageRepository.saveDoseList(doseList)
                        readMessage.value = Res.string.csv_loaded
                        isReading.value = false
                    }
                }
            }
        }
    }

    fun initDemoData() {
        coroutineScope.launch {
            storageRepository.saveDoseList(generateDoseData("ABCD1234", "EFGH5678"))
            getPenList().first().firstOrNull { it.serial == "ABCD1234" }?.let {
                storageRepository.updatePen(it.copy(name = "NovoRapid", color = "99ff99"))
            }
            getPenList().first().firstOrNull { it.serial == "EFGH5678" }?.let {
                storageRepository.updatePen(it.copy(name = "Tresiba", color = "ffcccc"))
            }
        }
    }
}
