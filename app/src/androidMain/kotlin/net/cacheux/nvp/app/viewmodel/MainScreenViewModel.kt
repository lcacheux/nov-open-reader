package net.cacheux.nvp.app.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import net.cacheux.nvp.app.repository.PenInfoRepository
import net.cacheux.nvp.app.repository.PreferencesRepository
import net.cacheux.nvp.app.repository.StorageRepository
import net.cacheux.nvp.app.usecase.DoseListUseCase
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    repository: PenInfoRepository,
    storageRepository: StorageRepository,
    preferencesRepository: PreferencesRepository,
): BaseMainScreenViewModel(
    repository, storageRepository,
    DoseListUseCase(storageRepository, preferencesRepository),
)
