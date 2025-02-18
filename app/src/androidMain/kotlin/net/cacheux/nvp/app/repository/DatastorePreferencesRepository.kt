package net.cacheux.nvp.app.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import net.cacheux.nvp.app.PreferencesRepository
import net.cacheux.nvp.app.utils.PreferenceStateFlowWrapper
import net.cacheux.nvplib.utils.StateFlowWrapper

private val Context.dataStore by preferencesDataStore(name = "settings")

class DatastorePreferencesRepository(context: Context): PreferencesRepository {
    private val dataStore = context.dataStore

    private object PreferencesKeys {
        val GROUP_ENABLED = booleanPreferencesKey("group_enabled")
        val GROUP_DELAY = intPreferencesKey("group_delay")
        val AUTO_IGNORE_ENABLED = booleanPreferencesKey("auto_ignore_enabled")
        val AUTO_IGNORE_VALUE = intPreferencesKey("auto_ignore_value")
        val GROUP_IOB = booleanPreferencesKey("group_iob")
        val IOB_INSULIN_PEAK = intPreferencesKey("iob_insulin_peak")
        val IOB_DELTA = intPreferencesKey("iob_delta")
        val IOB_INSULIN_DURATION = intPreferencesKey("iob_insulin_duration")
    }

    override val groupEnabled: StateFlowWrapper<Boolean> =
        PreferenceStateFlowWrapper(
            dataStore, PreferencesKeys.GROUP_ENABLED, true
        )

    override val groupDelay: StateFlowWrapper<Int> =
        PreferenceStateFlowWrapper(
            dataStore, PreferencesKeys.GROUP_DELAY, 60
        )

    override val autoIgnoreEnabled: StateFlowWrapper<Boolean> =
        PreferenceStateFlowWrapper(
            dataStore, PreferencesKeys.AUTO_IGNORE_ENABLED, true
        )

    override val autoIgnoreValue: StateFlowWrapper<Int> =
        PreferenceStateFlowWrapper(
            dataStore, PreferencesKeys.AUTO_IGNORE_VALUE, 2
        )

    override val groupIoB: StateFlowWrapper<Boolean> = PreferenceStateFlowWrapper(
        dataStore, PreferencesKeys.GROUP_IOB, false
    )

    override val insulinPeak: StateFlowWrapper<Int> = PreferenceStateFlowWrapper(
        dataStore, PreferencesKeys.IOB_INSULIN_PEAK, 75
    )

    override val delta: StateFlowWrapper<Int> = PreferenceStateFlowWrapper(
        dataStore, PreferencesKeys.IOB_DELTA, 15
    )

    override val insulinDuration: StateFlowWrapper<Int> = PreferenceStateFlowWrapper(
        dataStore, PreferencesKeys.IOB_INSULIN_DURATION, 5
    )
}
