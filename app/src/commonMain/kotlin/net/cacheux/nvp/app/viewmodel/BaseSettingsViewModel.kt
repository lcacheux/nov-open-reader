package net.cacheux.nvp.app.viewmodel

import androidx.lifecycle.ViewModel
import net.cacheux.nvp.app.repository.PreferencesRepository

open class BaseSettingsViewModel(
    preferencesRepository: PreferencesRepository
): ViewModel() {
    val theme = preferencesRepository.theme
    val groupEnabled = preferencesRepository.groupEnabled
    val groupDelay = preferencesRepository.groupDelay
    val autoIgnoreEnabled = preferencesRepository.autoIgnoreEnabled
    val autoIgnoreValue = preferencesRepository.autoIgnoreValue
}