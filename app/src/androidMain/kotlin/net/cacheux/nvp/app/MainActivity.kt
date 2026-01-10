package net.cacheux.nvp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.cacheux.nvp.app.BuildConfig.DEMO_VERSION
import net.cacheux.nvp.app.repository.ActivityRequirer
import net.cacheux.nvp.app.repository.PenInfoRepository
import net.cacheux.nvp.app.repository.Theme
import net.cacheux.nvp.app.ui.ScreenWrapper
import net.cacheux.nvp.app.utils.csvFilename
import net.cacheux.nvp.app.utils.toCsv
import net.cacheux.nvp.app.viewmodel.MainScreenViewModel
import net.cacheux.nvp.app.viewmodel.PenSettingsViewModel
import net.cacheux.nvp.app.viewmodel.SettingsViewModel
import net.cacheux.nvp.ui.MainDropdownMenuActions
import net.cacheux.nvp.ui.asStateWrapper
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: PenInfoRepository

    private val mainScreenViewModel: MainScreenViewModel by viewModels()
    private val penSettingsViewModel: PenSettingsViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val isDark = when(settingsViewModel.theme.asStateWrapper().value) {
                Theme.THEME_LIGHT -> false
                Theme.THEME_DARK -> true
                Theme.THEME_SYSTEM -> isSystemInDarkTheme()
                else -> false
            }

            MaterialTheme(
                colorScheme = if (isDark) darkColorScheme() else lightColorScheme()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ScreenWrapper(
                        mainScreenViewModel = mainScreenViewModel,
                        penSettingsViewModel, settingsViewModel,

                        MainDropdownMenuActions(
                            onSaveStore = { saveRawFile() },
                            onExportCsv = { saveCsvFile() },
                            onImportCsv = { loadCsvFile() },
                            onInitDemo = { mainScreenViewModel.initDemoData() }
                        ),
                        demoVersion = DEMO_VERSION
                    )
                }
            }
        }
    }

    private val saveRawFile =
        registerForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri ->
            uri?.let {
                contentResolver.openOutputStream(it)?.let { output ->
                    repository.getDataStore().value?.toOutputStream(output)
                }
            }
        }

    private val saveCsvFile =
        registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
            uri?.let {
                CoroutineScope(Dispatchers.IO).launch {
                    val content = mainScreenViewModel.flatDoseList.first().toCsv()
                    contentResolver.openOutputStream(it)?.write(content.toByteArray())
                }
            }
        }

    private val loadCsvFile =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { safeUri ->
                contentResolver.openInputStream(safeUri)?.let {
                    mainScreenViewModel.loadCsvFile(it)
                }
            }
        }

    private fun saveRawFile() {
        saveRawFile.launch("nvp_data.txt")
    }

    private fun saveCsvFile() {
        saveCsvFile.launch("${csvFilename(mainScreenViewModel.getCurrentPen().value)}.csv")
    }

    private fun loadCsvFile() {
        loadCsvFile.launch("text/*")
    }

    override fun onStart() {
        super.onStart()

        if (repository is ActivityRequirer) {
            (repository as ActivityRequirer).setActivity(this)
        }
    }

    override fun onStop() {
        if (repository is ActivityRequirer) {
            (repository as ActivityRequirer).setActivity(null)
        }

        super.onStop()
    }
}
