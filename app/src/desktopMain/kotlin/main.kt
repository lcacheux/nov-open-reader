import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.compose.rememberFileSaverLauncher
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import net.cacheux.nvp.app.DoseListUseCase
import net.cacheux.nvp.app.IoBUseCase
import net.cacheux.nvp.app.MainScreenViewModel
import net.cacheux.nvp.app.SettingsViewModel
import net.cacheux.nvp.app.StorageRepository
import net.cacheux.nvp.app.TestPenInfoRepository
import net.cacheux.nvp.app.repository.PreferencesRepositoryImpl
import net.cacheux.nvp.app.utils.toCsv
import net.cacheux.nvp.ui.MainDropdownMenuActions
import net.cacheux.nvp.ui.MainScreen
import net.cacheux.nvp.ui.SettingsScreen
import net.cacheux.nvp.ui.SettingsScreenParams
import net.cacheux.nvp.ui.SideMenuParams
import net.cacheux.nvp.ui.asStateWrapper
import net.cacheux.nvplib.storage.room.NvpDatabase
import net.cacheux.nvplib.storage.room.RoomDoseStorage

val storageRepository = StorageRepository(RoomDoseStorage(
    Room.databaseBuilder<NvpDatabase>(name = "nvp.db")
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
))

val preferencesRepository = PreferencesRepositoryImpl()

val mainScreenViewModel = MainScreenViewModel(
    TestPenInfoRepository(),
    doseListUseCase = DoseListUseCase(storageRepository, preferencesRepository),
    storageRepository = storageRepository,
    iobUseCase = IoBUseCase(preferencesRepository)
)

val settingsViewModel = SettingsViewModel(preferencesRepository)

val ioScope = CoroutineScope(Dispatchers.IO)

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Nov Open Reader",
    ) {


        val loadRawDataPicker = rememberFilePickerLauncher(
            type = PickerType.File(),
            title = "Load file"
        ) { file ->
            file?.let {
                mainScreenViewModel.loadFromFile(it)
            }
        }

        val loadCsvPicker = rememberFilePickerLauncher(
            type = PickerType.File(listOf("csv")),
            title = "Load CSV"
        ) { file ->
            file?.let {
                mainScreenViewModel.loadCsvFile(it)
            }
        }

        val saveCsvPicker = rememberFileSaverLauncher { file ->
            // TODO feedback message
        }

        var settingsOpened by remember { mutableStateOf(false) }

        if (settingsOpened) {
            SettingsScreen(
                params = SettingsScreenParams(
                    onBack = { settingsOpened = false },
                    groupDose = settingsViewModel.groupEnabled.asStateWrapper(),
                    groupDelay = settingsViewModel.groupDelay.asStateWrapper(),
                    autoIgnoreEnabled = settingsViewModel.autoIgnoreEnabled.asStateWrapper(),
                    autoIgnoreValue = settingsViewModel.autoIgnoreValue.asStateWrapper(),
                )
            )
        } else {
            MainScreen(
                doseList = mainScreenViewModel.doseList.collectAsState(listOf()).value.reversed(),
                iob = mainScreenViewModel.iob.collectAsState(null).value,

                loadingFileAvailable = true,

                loading = mainScreenViewModel.isReading().collectAsState().value,
                message = mainScreenViewModel.getReadMessage().collectAsState().value,
                onDismissMessage = { mainScreenViewModel.clearPopup() },

                storeAvailable = mainScreenViewModel.store.collectAsState().value != null,

                dropdownMenuActions = MainDropdownMenuActions(
                    onLoadingClick = { loadRawDataPicker.launch() },
                    onExportCsv = {
                        ioScope.launch {
                            saveCsvPicker.launch(
                                baseName = mainScreenViewModel.getCurrentPen().value?.let {
                                    "nvp_export_$it"
                                } ?: "nvp_export_all",
                                extension = "csv",
                                bytes = mainScreenViewModel.flatDoseList.first().toCsv().toByteArray()
                            )
                        }
                    },
                    onImportCsv = { loadCsvPicker.launch() }
                ),

                sideMenuParams = SideMenuParams(
                    penList = mainScreenViewModel.getPenList()
                        .collectAsState(listOf()).value.map { it.serial },
                    selectedPen = mainScreenViewModel.getCurrentPen().collectAsState().value,
                    onItemClick = { mainScreenViewModel.setCurrentPen(it) },
                    onSettingsClick = { settingsOpened = true }
                )
            )
        }
    }
}
