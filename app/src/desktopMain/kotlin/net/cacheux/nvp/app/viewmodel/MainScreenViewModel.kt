package net.cacheux.nvp.app.viewmodel

import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.cacheux.nvp.app.repository.PenInfoRepository
import net.cacheux.nvp.app.repository.StorageRepository
import net.cacheux.nvp.app.usecase.DoseListUseCase
import net.cacheux.nvp.logging.logDebug
import net.cacheux.nvplib.NvpController
import net.cacheux.nvplib.testing.TestingDataReader

class MainScreenViewModel(
    private val repository: PenInfoRepository,
    private val storageRepository: StorageRepository,
    private val doseListUseCase: DoseListUseCase,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
): BaseMainScreenViewModel(
    repository, storageRepository, doseListUseCase, coroutineScope
) {
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

    fun loadCsvFile(file: PlatformFile) {
        coroutineScope.launch {
            loadCsvFile(file.readBytes().inputStream())
        }
    }
}
