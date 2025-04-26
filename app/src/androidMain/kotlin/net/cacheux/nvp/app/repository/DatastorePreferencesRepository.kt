package net.cacheux.nvp.app.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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
}
