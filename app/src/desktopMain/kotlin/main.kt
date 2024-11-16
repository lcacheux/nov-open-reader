import androidx.compose.runtime.collectAsState
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.cacheux.nvp.app.MainScreenViewModel
import net.cacheux.nvp.app.StorageRepository
import net.cacheux.nvp.app.TestPenInfoRepository
import net.cacheux.nvp.model.DoseGroup
import net.cacheux.nvp.ui.MainScreen
import net.cacheux.nvp.ui.SideMenuParams
import net.cacheux.nvplib.storage.room.NvpDatabase
import net.cacheux.nvplib.storage.room.RoomDoseStorage

val viewModel = MainScreenViewModel(
    TestPenInfoRepository(),
    StorageRepository(RoomDoseStorage(
        Room.databaseBuilder<NvpDatabase>(name = "nvp.db")
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    ))
)

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Nov Open Reader",
    ) {
        val launcher = rememberFilePickerLauncher(
            type = PickerType.File(),
            title = "Load file"
        ) { file ->
            file?.let {
                val scope = CoroutineScope(Dispatchers.IO)
                scope.launch {
                    viewModel.loadFromFile(it.readBytes().inputStream())
                }
            }
        }

        MainScreen(
            doseList = DoseGroup.createDoseGroups(viewModel.doseList.collectAsState(listOf()).value).reversed(),
            loadingFileAvailable = true,

            storeAvailable = viewModel.store.collectAsState().value != null,
            onLoadingClick = { launcher.launch() },
            onSaveStore = {
                //saveToFile()
            },
            sideMenuParams = SideMenuParams(
                penList = viewModel.getPenList().collectAsState(listOf()).value.map { it.serial },
                selectedPen = viewModel.getCurrentPen().collectAsState().value,
                onItemClick = { viewModel.setCurrentPen(it) }
            )
        )
    }
}
