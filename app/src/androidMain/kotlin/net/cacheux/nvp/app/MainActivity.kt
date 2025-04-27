package net.cacheux.nvp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.cacheux.nvp.app.repository.ActivityRequirer
import net.cacheux.nvp.app.repository.PenInfoRepository
import net.cacheux.nvp.app.ui.ScreenWrapper
import net.cacheux.nvp.app.utils.csvFilename
import net.cacheux.nvp.app.utils.toCsv
import net.cacheux.nvp.app.viewmodel.MainScreenViewModel
import net.cacheux.nvp.app.viewmodel.PenSettingsViewModel
import net.cacheux.nvp.app.viewmodel.SettingsViewModel
import net.cacheux.nvp.ui.MainDropdownMenuActions
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
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    ScreenWrapper(
                        mainScreenViewModel = mainScreenViewModel,
                        penSettingsViewModel, settingsViewModel,

                        MainDropdownMenuActions(
                            onSaveStore = { saveRawFile() },
                            onExportCsv = { saveCsvFile() },
                            onImportCsv = { loadCsvFile() }
                        )
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
