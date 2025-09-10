package net.cacheux.nvp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import net.cacheux.nvp.model.DoseGroup
import net.cacheux.nvp.model.testDateTime
import net.cacheux.nvp.model.testDoseGroup
import net.cacheux.nvp.ui.ui.generated.resources.Res
import net.cacheux.nvp.ui.ui.generated.resources.app_name
import net.cacheux.nvp.ui.ui.generated.resources.open_drawer
import net.cacheux.nvp.ui.ui.generated.resources.open_menu
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    doseList: List<DoseGroup>,
    message: String? = null,

    loading: Boolean = false,
    onDismissMessage: () -> Unit = {},

    sideMenuParams: SideMenuParams = SideMenuParams(),
    dropdownMenuParams: MainDropdownMenuParams = MainDropdownMenuParams(),
    dropdownMenuActions: MainDropdownMenuActions = MainDropdownMenuActions()
) {
    val currentDoseGroup = remember { mutableStateOf<DoseGroup?>(null) }

    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    BackHandlerWrapper(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    message?.let {
        StatusPopup(
            message = it,
            displayLoader = loading,
            onDismiss = onDismissMessage
        )
    }

    BackHandlerWrapper(enabled = message != null) {
        if (!loading) onDismissMessage()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SideMenu(
                SideMenuParams(
                    penList = sideMenuParams.penList,
                    selectedPen = sideMenuParams.selectedPen,
                    onItemClick = {
                        sideMenuParams.onItemClick(it)
                        scope.launch { drawerState.close() }
                    },
                    onPenSettingsClick = sideMenuParams.onPenSettingsClick,
                    onSettingsClick = sideMenuParams.onSettingsClick
                )
            )
        }
    ) {
        Scaffold(
            topBar = {
                CustomTopBar(
                    onNavClick = {
                        scope.launch {
                            drawerState.open()
                        }
                    },
                    mainDropdownMenuParams = dropdownMenuParams,
                    dropdownMenuActions = dropdownMenuActions
                )
            },
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                DoseList(
                    doseList,
                    currentDoseGroup = currentDoseGroup.value,
                    onDoseClick = {
                        scope.launch {
                            if (currentDoseGroup.value == it) {
                                currentDoseGroup.value = null
                            } else {
                                currentDoseGroup.value = it
                            }
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopBar(
    onNavClick: () -> Unit = {},
    mainDropdownMenuParams: MainDropdownMenuParams = MainDropdownMenuParams(),
    dropdownMenuActions: MainDropdownMenuActions = MainDropdownMenuActions(),
) {
    var dropdownOpened by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(text = stringResource(Res.string.app_name))
        },
        navigationIcon = {
            IconButton(
                onClick = onNavClick,
                modifier = Modifier.testTag("open_drawer_button"),
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = stringResource(Res.string.open_drawer)
                )
            }
        },
        actions = {
            IconButton(onClick = { dropdownOpened = true }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = stringResource(Res.string.open_menu)
                )
            }

            MainDropdownMenu(
                opened = dropdownOpened,
                onDismiss = { dropdownOpened = false },
                params = mainDropdownMenuParams,
                actions = dropdownMenuActions.and { dropdownOpened = false }
            )
        },
    )
}

@Composable
fun ItemList(items: List<DoseGroup>, onDoseClick: (DoseGroup) -> Unit = {}) {
    LazyColumn {
        items(items) { item ->
            DoseDisplay(dose = item, onClick = onDoseClick)
        }
    }
}

@Composable
fun DoseDisplay(
    dose: DoseGroup,
    onClick: (DoseGroup) -> Unit  = {}
) {
    val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.surface)
            .clickable { onClick(dose) }
    ) {
        Column(
            modifier = Modifier
                .padding(4.dp)
        ) {
            Text(
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic,
                text = format.format(Date(dose.getTime()))
            )
            Text(
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
                text = dose.getTotal().toString()
            )
            HorizontalDivider()
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    val items = listOf(
        testDoseGroup(testDateTime(12, 1, 12), 12),
        testDoseGroup(testDateTime(12, 1, 13), 13),
        testDoseGroup(testDateTime(12, 1, 14), 14),
    )
    MainScreen(items)
}


@Preview
@Composable
fun PreviewItemList() {
    val items = listOf(
        testDoseGroup(testDateTime(12, 1, 12), 12),
        testDoseGroup(testDateTime(12, 1, 13), 13),
        testDoseGroup(testDateTime(12, 1, 14), 14),
    )
    ItemList(items)
}

@Preview
@Composable
fun PreviewDoseDisplay() {
    val dose = testDoseGroup(testDateTime(12, 2, 23), 42)
    DoseDisplay(dose)
}
