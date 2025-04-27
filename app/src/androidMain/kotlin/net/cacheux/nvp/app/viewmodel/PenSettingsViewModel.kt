package net.cacheux.nvp.app.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import net.cacheux.nvp.app.repository.StorageRepository
import javax.inject.Inject

@HiltViewModel
class PenSettingsViewModel @Inject constructor(
    storageRepository: StorageRepository
) : BasePenSettingsViewModel(storageRepository)