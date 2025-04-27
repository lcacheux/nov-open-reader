package net.cacheux.nvp.app.repository

import net.cacheux.nvplib.utils.StateFlowWrapper

interface PreferencesRepository {
    val groupEnabled: StateFlowWrapper<Boolean>
    val groupDelay: StateFlowWrapper<Int>
    val autoIgnoreEnabled: StateFlowWrapper<Boolean>
    val autoIgnoreValue: StateFlowWrapper<Int>
}
