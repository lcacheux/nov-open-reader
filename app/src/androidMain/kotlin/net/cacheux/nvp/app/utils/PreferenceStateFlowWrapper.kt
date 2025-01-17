package net.cacheux.nvp.app.utils


import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import net.cacheux.nvplib.utils.StateFlowWrapper

class PreferenceStateFlowWrapper<T>(
    private val datastore: DataStore<Preferences>,
    private val key: Preferences.Key<T>,
    private val initialValue: T,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
): StateFlowWrapper<T>
{
    override val content: StateFlow<T>
        get() {
            return datastore.data.map { preferences ->
                preferences[key] ?: initialValue
            }.stateIn(
                scope = scope,
                started = SharingStarted.Eagerly,
                initialValue = initialValue
            )
        }

    override val value: T = content.value

    override val set: (T) -> Unit = { value ->
        scope.launch {
            datastore.edit { preferences ->
                preferences[key] = value
            }
        }
    }
}
