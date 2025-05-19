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
import net.cacheux.nvp.app.repository.PreferencesRepositoryImpl
import net.cacheux.nvp.app.repository.StorageRepository
import net.cacheux.nvp.app.repository.TestPenInfoRepository
import net.cacheux.nvp.app.ui.ScreenWrapper
import net.cacheux.nvp.app.usecase.DoseListUseCase
import net.cacheux.nvp.app.utils.csvFilename
import net.cacheux.nvp.app.utils.toCsv
import net.cacheux.nvp.app.viewmodel.BasePenSettingsViewModel
import net.cacheux.nvp.app.viewmodel.BaseSettingsViewModel
import net.cacheux.nvp.app.viewmodel.MainScreenViewModel
import net.cacheux.nvp.ui.MainDropdownMenuActions
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
    storageRepository = storageRepository
)

val settingsViewModel = BaseSettingsViewModel(preferencesRepository)

val penSettingsViewModel = BasePenSettingsViewModel(storageRepository)

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

        ScreenWrapper(
            mainScreenViewModel = mainScreenViewModel,
            penSettingsViewModel = penSettingsViewModel,
            settingsViewModel = settingsViewModel,

            dropdownMenuActions = MainDropdownMenuActions(
                onLoadingClick = { loadRawDataPicker.launch() },
                onExportCsv = {
                    ioScope.launch {
                        saveCsvPicker.launch(
                            baseName = csvFilename(mainScreenViewModel.getCurrentPen().value),
                            extension = "csv",
                            bytes = mainScreenViewModel.flatDoseList.first().toCsv().toByteArray()
                        )
                    }
                },
                onImportCsv = { loadCsvPicker.launch() },
                onInitDemo = { mainScreenViewModel.initDemoData() }
            ),

            demoVersion = true
        )
    }
}
