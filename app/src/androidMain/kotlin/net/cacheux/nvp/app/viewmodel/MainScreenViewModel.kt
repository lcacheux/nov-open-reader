package net.cacheux.nvp.app.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import net.cacheux.nvp.app.repository.PenInfoRepository
import net.cacheux.nvp.app.repository.PreferencesRepository
import net.cacheux.nvp.app.repository.StorageRepository
import net.cacheux.nvp.app.usecase.DoseListUseCase
import net.cacheux.nvp.ui.ui.generated.resources.Res
import net.cacheux.nvp.ui.ui.generated.resources.reading_pen
import net.cacheux.nvp.ui.ui.generated.resources.reading_pen_error
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    repository: PenInfoRepository,
    private val storageRepository: StorageRepository,
    preferencesRepository: PreferencesRepository,
): BaseMainScreenViewModel(
    repository, storageRepository,
    DoseListUseCase(storageRepository, preferencesRepository),
) {
    init {
        repository.registerOnDataReceivedCallback { result ->
            coroutineScope.launch {
                storageRepository.saveResult(result)
            }
        }

        repository.registerCallbacks(
            PenInfoRepository.Callbacks(
            onReadStart = {
                isReading.value = true
                readMessage.value = Res.string.reading_pen
            },
            onReadStop = {
                isReading.value = false
                readMessage.value = null
            },
            onError = { e ->
                isReading.value = false
                readMessage.value = Res.string.reading_pen_error
            }
        ))
    }
}
