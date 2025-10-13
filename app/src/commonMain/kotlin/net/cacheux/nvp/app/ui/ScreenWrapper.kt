package net.cacheux.nvp.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import net.cacheux.nvp.app.viewmodel.BaseMainScreenViewModel
import net.cacheux.nvp.app.viewmodel.BasePenSettingsViewModel
import net.cacheux.nvp.app.viewmodel.BaseSettingsViewModel
import net.cacheux.nvp.ui.BackHandlerWrapper
import net.cacheux.nvp.ui.MainDropdownMenuActions
import net.cacheux.nvp.ui.MainDropdownMenuParams
import net.cacheux.nvp.ui.MainScreen
import net.cacheux.nvp.ui.PenSettingsScreen
import net.cacheux.nvp.ui.PenSettingsScreenParams
import net.cacheux.nvp.ui.SettingsScreen
import net.cacheux.nvp.ui.SettingsScreenParams
import net.cacheux.nvp.ui.SideMenuParams
import net.cacheux.nvp.ui.asStateWrapper
import org.jetbrains.compose.resources.stringResource

enum class CurrentScreen {
    Main,
    PenSettings,
    Settings
}

@Composable
fun ScreenWrapper(
    mainScreenViewModel: BaseMainScreenViewModel,
    penSettingsViewModel: BasePenSettingsViewModel,
    settingsViewModel: BaseSettingsViewModel,
    dropdownMenuActions: MainDropdownMenuActions,
    demoVersion: Boolean = false
) {
    var currentScreen by remember { mutableStateOf(CurrentScreen.Main) }

    BackHandlerWrapper(enabled = currentScreen != CurrentScreen.Main) {
        if (currentScreen != CurrentScreen.Main) {
            currentScreen = CurrentScreen.Main
        }
    }

    when(currentScreen) {
        CurrentScreen.Main -> {
            MainScreen(
                doseList = mainScreenViewModel.doseList.collectAsState(listOf()).value.reversed(),
                loading = mainScreenViewModel.isReading().collectAsState().value,
                message = mainScreenViewModel.getReadMessage().collectAsState().value?.let {
                    stringResource(it)
                },
                onDismissMessage = { mainScreenViewModel.clearPopup() },

                dropdownMenuParams = MainDropdownMenuParams(
                    loadingFileAvailable = true,
                    storeAvailable = mainScreenViewModel.store.collectAsState().value != null,
                    demoVersion = demoVersion
                ),

                dropdownMenuActions = dropdownMenuActions,

                sideMenuParams = SideMenuParams(
                    penList = mainScreenViewModel.getPenList().collectAsState(listOf()).value,
                    selectedPen = mainScreenViewModel.getCurrentPen().collectAsState().value,
                    onItemClick = { mainScreenViewModel.setCurrentPen(it) },
                    onPenSettingsClick = {
                        currentScreen = CurrentScreen.PenSettings
                    },
                    onSettingsClick = {
                        currentScreen = CurrentScreen.Settings
                    }
                )
            )
        }

        CurrentScreen.PenSettings -> {
            PenSettingsScreen(
                params = PenSettingsScreenParams(
                    onBack = { currentScreen = CurrentScreen.Main },
                    penList = penSettingsViewModel.getPenList()
                        .collectAsState(listOf()).value,
                    onNameChanged = { serial, name -> penSettingsViewModel.updatePenName(serial, name) },
                    onColorChanged = { serial, color -> penSettingsViewModel.updatePenColor(serial, color) },
                    onDeletePen = { serial -> penSettingsViewModel.deletePen(serial) },
                )
            )
        }
        CurrentScreen.Settings -> {
            SettingsScreen(
                params = SettingsScreenParams(
                    onBack = { currentScreen = CurrentScreen.Main },
                    groupDose = settingsViewModel.groupEnabled.asStateWrapper(),
                    groupDelay = settingsViewModel.groupDelay.asStateWrapper(),
                    autoIgnoreEnabled = settingsViewModel.autoIgnoreEnabled.asStateWrapper(),
                    autoIgnoreValue = settingsViewModel.autoIgnoreValue.asStateWrapper(),
                )
            )
        }
    }
}