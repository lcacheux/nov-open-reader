package net.cacheux.nvp.app

import net.cacheux.nvp.model.Dose
import net.cacheux.nvplib.utils.stateFlowWrapper
import org.mockito.kotlin.mock
import java.util.Date

fun mockPreferencesRepository(
    groupEnabled: Boolean,
    groupDelay: Int = 60,
    autoIgnoreEnabled: Boolean = true,
    autoIgnoreValue: Int = 2
) = mock<PreferencesRepository> {
    on { this.groupEnabled }.thenReturn(stateFlowWrapper(groupEnabled))
    on { this.groupDelay }.thenReturn(stateFlowWrapper(groupDelay))
    on { this.autoIgnoreEnabled }.thenReturn(stateFlowWrapper(autoIgnoreEnabled))
    on { this.autoIgnoreValue }.thenReturn(stateFlowWrapper(autoIgnoreValue))
}

fun genDoses(start: Date, delay: Int, vararg values: Int, serial: String = ""): List<Dose> =
    values.mapIndexed { index, i -> Dose(start.time + delay * index, i, serial = serial) }
