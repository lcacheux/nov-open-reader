package net.cacheux.nvp.app

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import net.cacheux.nvp.app.utils.csvToDoseList
import net.cacheux.nvp.logging.logDebug
import net.cacheux.nvp.model.Dose
import net.cacheux.nvp.model.DoseGroup
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: PenInfoRepository,
    private val storageRepository: StorageRepository,
    private val preferencesRepository: PreferencesRepository,
): ViewModel() {
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    private val doseListUseCase: DoseListUseCase = DoseListUseCase(
        storageRepository,
        preferencesRepository
    )

    fun getPenList() = storageRepository.getPenList()

    private val isReading = MutableStateFlow(false)
    fun isReading(): StateFlow<Boolean> = isReading

    private val readMessage = MutableStateFlow<String?>(null)
    fun getReadMessage(): StateFlow<String?> = readMessage

    fun onDismissMessage() {
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

    val store = repository.getDataStore()

    fun loadCsvFile(input: InputStream) {
        input.reader().use {
            it.readText().csvToDoseList().let { doseList ->
                if (doseList.isEmpty()) {
                    readMessage.value = context.resources.getString(R.string.no_csv_data)
                } else {
                    coroutineScope.launch {
                        readMessage.value = context.resources.getString(R.string.loading_csv)
                        isReading.value = true
                        storageRepository.saveDoseList(doseList)
                        readMessage.value = context.resources.getString(R.string.csv_loaded)
                        isReading.value = false
                    }
                }
            }
        }
    }

    init {
        repository.registerOnDataReceivedCallback { result ->
            coroutineScope.launch {
                storageRepository.saveResult(result)
            }
        }

        repository.registerCallbacks(PenInfoRepository.Callbacks(
            onReadStart = {
                isReading.value = true
                readMessage.value = context.resources.getString(R.string.reading_pen)
            },
            onReadStop = {
                isReading.value = false
                readMessage.value = null
            },
            onError = { e ->
                isReading.value = false
                readMessage.value = e.message
            }
        ))
    }
}
