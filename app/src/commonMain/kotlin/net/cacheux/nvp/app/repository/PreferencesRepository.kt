package net.cacheux.nvp.app.repository

import net.cacheux.nvplib.utils.StateFlowWrapper

object Theme {
    const val THEME_LIGHT = 0
    const val THEME_DARK = 1
    const val THEME_SYSTEM = 2
}

interface PreferencesRepository {
    val theme: StateFlowWrapper<Int>

    val groupEnabled: StateFlowWrapper<Boolean>
    val groupDelay: StateFlowWrapper<Int>
    val autoIgnoreEnabled: StateFlowWrapper<Boolean>
    val autoIgnoreValue: StateFlowWrapper<Int>
}
