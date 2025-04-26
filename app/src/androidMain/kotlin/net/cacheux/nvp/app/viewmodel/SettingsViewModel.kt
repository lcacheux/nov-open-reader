package net.cacheux.nvp.app.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import net.cacheux.nvp.app.repository.PreferencesRepository
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    preferencesRepository: PreferencesRepository
) : BaseSettingsViewModel(preferencesRepository)