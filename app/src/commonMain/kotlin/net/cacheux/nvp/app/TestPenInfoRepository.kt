package net.cacheux.nvp.app

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import net.cacheux.nvplib.utils.ByteArrayStore

class TestPenInfoRepository: BasePenInfoRepository() {
    override fun getDataStore(): StateFlow<ByteArrayStore?> = MutableStateFlow(null)
    override fun registerCallbacks(callbacks: PenInfoRepository.Callbacks) {
        // Ignore
    }
}
