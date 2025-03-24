package net.cacheux.nvp.app

import net.cacheux.nvplib.utils.StateFlowWrapper

interface PreferencesRepository {
    val groupEnabled: StateFlowWrapper<Boolean>
    val groupDelay: StateFlowWrapper<Int>
    val autoIgnoreEnabled: StateFlowWrapper<Boolean>
    val autoIgnoreValue: StateFlowWrapper<Int>

    val groupIoB: StateFlowWrapper<Boolean>
    // Time to peak insulin activity (Novorapid: 75 minutes)
    val insulinPeak: StateFlowWrapper<Int>
    // Time period to calculate amount of current active insulin
    val delta: StateFlowWrapper<Int>
    // How old doses is calculated (Novorapid: 5 hours)
    val insulinDuration: StateFlowWrapper<Int>
}
