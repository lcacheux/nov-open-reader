package net.cacheux.nvp.app

import androidx.activity.viewModels
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import net.cacheux.nvp.app.repository.StorageRepository
import net.cacheux.nvp.app.viewmodel.MainScreenViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(NvpModule::class)
@RunWith(AndroidJUnit4::class)
class CsvImportTest {
    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Inject
    lateinit var storageRepository: StorageRepository

    @Test
    fun testCsvImport() = runBlocking {
        val csv1 = """
Serial;Timestamp;Time;Value
ABCD1234;1704110400000;2024-01-01T13:00:00.000+0100;20
ABCD1234;1704110400000;2024-01-01T13:00:00.000+0100;20
ABCD1234;1704110410000;2024-01-01T13:00:10.000+0100;40
ABCD1234;1704110420000;2024-01-01T13:00:20.000+0100;160
ABCD1234;1704110440000;2024-01-01T13:00:40.000+0100;160
ABCD1234;1704111000000;2024-01-01T13:10:00.000+0100;20
ABCD1234;1704111010000;2024-01-01T13:10:10.000+0100;60
ABCD1234;1704111020000;2024-01-01T13:10:20.000+0100;400
ABCD5678;1704111030000;2024-01-01T13:10:30.000+0100;20
ABCD5678;1704111040000;2024-01-01T13:10:40.000+0100;520
        """.trimIndent()

        val csv2 = """
Serial;Timestamp;Time;Value
ABCD1234;1704110400000;2024-01-01T13:00:00.000+0100;20
ABCD1234;1704110400000;2024-01-01T13:00:00.000+0100;20
ABCD1234;1704110410000;2024-01-01T13:00:10.000+0100;40
ABCD1234;1704111020000;2024-01-01T13:10:20.000+0100;400
CDEF5678;1704111030000;2024-01-01T13:10:30.000+0100;20
CDEF5678;1704111040000;2024-01-01T13:10:40.000+0100;520
        """.trimIndent()

        val mainScreenViewModel = composeTestRule.activity.viewModels<MainScreenViewModel>().value

        mainScreenViewModel.isReading().test {
            assertFalse(awaitItem())

            mainScreenViewModel.loadCsvFile(csv1.byteInputStream())

            assertTrue(awaitItem())
            assertFalse(awaitItem())

            assertEquals(2, storageRepository.getPenList().first().size)
            assertEquals(10, storageRepository.getDoseList().first().size)

            mainScreenViewModel.loadCsvFile(csv2.byteInputStream())

            assertTrue(awaitItem())
            assertFalse(awaitItem())

            assertEquals(3, storageRepository.getPenList().first().size)
            assertEquals(12, storageRepository.getDoseList().first().size)
        }
    }
}