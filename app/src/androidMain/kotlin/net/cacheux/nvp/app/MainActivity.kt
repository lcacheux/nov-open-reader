package net.cacheux.nvp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.cacheux.nvp.app.repository.ActivityRequirer
import net.cacheux.nvp.app.utils.toCsv
import net.cacheux.nvp.model.DoseGroup
import net.cacheux.nvp.ui.MainDropdownMenuActions
import net.cacheux.nvp.ui.MainScreen
import net.cacheux.nvp.ui.SideMenuParams
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: PenInfoRepository

    private val viewModel: MainScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MainScreen(
                        doseList = DoseGroup.createDoseGroups(viewModel.doseList.collectAsState(listOf()).value).reversed(),
                        message = viewModel.getReadError().collectAsState().value,

                        loading = viewModel.isReading().collectAsState().value,
                        onDismissMessage = viewModel::onDismissMessage,
                        storeAvailable = viewModel.store.collectAsState().value != null,

                        dropdownMenuActions = MainDropdownMenuActions(
                            onSaveStore = { saveRawFile() },
                            onExportCsv = { saveCsvFile() }
                        ),

                        sideMenuParams = SideMenuParams(
                            penList = viewModel.getPenList().collectAsState(listOf()).value.map { it.serial },
                            selectedPen = viewModel.getCurrentPen().collectAsState().value,
                            onItemClick = { viewModel.setCurrentPen(it) }
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
                    val content = viewModel.doseList.first().toCsv()
                    contentResolver.openOutputStream(it)?.write(content.toByteArray())
                }
            }
        }

    private fun saveRawFile() {
        saveRawFile.launch("nvp_data.txt")
    }

    private fun saveCsvFile() {
        saveCsvFile.launch(
            viewModel.getCurrentPen().value?.let {
                "nvp_export_$it.csv"
            } ?: "nvp_export_all.csv"
        )
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
