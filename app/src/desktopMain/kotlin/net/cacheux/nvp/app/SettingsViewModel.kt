package net.cacheux.nvp.app

class SettingsViewModel(
    preferencesRepository: PreferencesRepository
) {
    val groupEnabled = preferencesRepository.groupEnabled
    val groupDelay = preferencesRepository.groupDelay
    val autoIgnoreEnabled = preferencesRepository.autoIgnoreEnabled
    val autoIgnoreValue = preferencesRepository.autoIgnoreValue
}