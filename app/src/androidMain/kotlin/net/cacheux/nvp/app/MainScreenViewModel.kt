package net.cacheux.nvp.app

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val repository: PenInfoRepository,
    private val storageRepository: StorageRepository,

): ViewModel() {
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    fun getPenList() = storageRepository.getPenList()

    fun isReading() = repository.isReading()
    fun getReadError() = repository.getReadMessage()

    fun onDismissMessage() {
        repository.clearReadMessage()
    }

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

    init {
        repository.registerOnDataReceivedCallback { result ->
            coroutineScope.launch {
                storageRepository.saveResult(result)
            }
        }
    }
}
