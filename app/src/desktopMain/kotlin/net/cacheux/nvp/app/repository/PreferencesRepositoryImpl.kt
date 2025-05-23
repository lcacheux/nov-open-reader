package net.cacheux.nvp.app.repository

import net.cacheux.nvplib.utils.stateFlowWrapper
import java.io.File
import java.util.Properties

class PreferencesRepositoryImpl: PreferencesRepository {
    private val propertiesFile = File("preferences.properties")
    private val properties = Properties()

    init {
        if (propertiesFile.exists()) {
            properties.load(propertiesFile.inputStream())
        }
    }

    override val groupEnabled = booleanStateFlowWrapper("groupEnabled", "true")
    override val groupDelay = intStateFlowWrapper("groupDelay", "60")
    override val autoIgnoreEnabled = booleanStateFlowWrapper("autoIgnoreEnabled", "true")
    override val autoIgnoreValue = intStateFlowWrapper("autoIgnoreValue", "2")

    private fun saveProperties() {
        propertiesFile.outputStream().use { properties.store(it, null) }
    }

    private fun intStateFlowWrapper(key: String, defaultValue: String) = stateFlowWrapper(
        properties.getProperty(key, defaultValue).toInt()
    ) {
        properties.setProperty(key, it.toString())
        saveProperties()
    }

    private fun booleanStateFlowWrapper(key: String, defaultValue: String) = stateFlowWrapper(
        properties.getProperty(key, defaultValue).toBoolean()
    ) {
        properties.setProperty(key, it.toString())
        saveProperties()
    }
}
