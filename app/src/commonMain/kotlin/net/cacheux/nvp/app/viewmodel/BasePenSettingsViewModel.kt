package net.cacheux.nvp.app.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.cacheux.nvp.app.repository.StorageRepository

open class BasePenSettingsViewModel(
    private val storageRepository: StorageRepository,
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
): ViewModel() {
    fun getPenList() = storageRepository.getPenList()

    fun updatePenName(serial: String, name: String) {
        coroutineScope.launch {
            getPenList().first().firstOrNull { it.serial == serial }?.let {
                storageRepository.updatePen(it.copy(name = name))
            }
        }
    }

    fun updatePenColor(serial: String, color: String) {
        coroutineScope.launch {
            getPenList().first().firstOrNull { it.serial == serial }?.let {
                storageRepository.updatePen(it.copy(color = color))
            }
        }
    }

    fun deletePen(serial: String) {
        coroutineScope.launch {
            storageRepository.deletePen(serial)
        }
    }
}