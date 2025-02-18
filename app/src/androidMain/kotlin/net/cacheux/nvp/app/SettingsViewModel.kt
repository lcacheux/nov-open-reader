package net.cacheux.nvp.app

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    preferencesRepository: PreferencesRepository
): ViewModel() {
    val groupEnabled = preferencesRepository.groupEnabled
    val groupDelay = preferencesRepository.groupDelay
    val autoIgnoreEnabled = preferencesRepository.autoIgnoreEnabled
    val autoIgnoreValue = preferencesRepository.autoIgnoreValue

    val groupIoB = preferencesRepository.groupIoB
    val insulinPeak = preferencesRepository.insulinPeak
    val delta = preferencesRepository.delta
    val insulinDuration = preferencesRepository.insulinDuration
}