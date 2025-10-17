package net.cacheux.nvp.app

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import net.cacheux.nvp.app.repository.PenInfoRepository
import net.cacheux.nvp.ui.ui.generated.resources.Res
import net.cacheux.nvp.ui.ui.generated.resources.delete_dose_warning
import net.cacheux.nvp.ui.ui.generated.resources.delete_selected
import net.cacheux.nvp.ui.ui.generated.resources.ok
import net.cacheux.nvp.ui.ui.generated.resources.open_drawer
import net.cacheux.nvp.ui.ui.generated.resources.reading_pen
import net.cacheux.nvplib.storage.DoseStorage
import org.jetbrains.compose.resources.getString
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@UninstallModules(NvpModule::class)
@RunWith(AndroidJUnit4::class)
class MainScreenTest {

    @Inject
    lateinit var doseStorage: DoseStorage

    @Inject
    lateinit var penInfoRepository: PenInfoRepository

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
    }

    @Test
    fun testBackButtonForPopup(): Unit = runBlocking {
        composeTestRule.run {
            val testingPenInfoRepository: TestingPenInfoRepository =
                penInfoRepository as TestingPenInfoRepository

            waitForIdle()

            onNodeWithContentDescription(getString(Res.string.open_drawer)).isDisplayed()

            testingPenInfoRepository.readStart()
            waitForIdle()

            onNodeWithText(getString(Res.string.reading_pen)).isDisplayed()

            pressBack()
            waitForIdle()

            // Pressing back when loading should not do anything
            onNodeWithText(getString(Res.string.reading_pen)).isDisplayed()

            testingPenInfoRepository.readError(Exception("Read error"))
            waitForIdle()

            onNodeWithText("Read error").isDisplayed()

            pressBack()
            waitForIdle()

            onNodeWithText("Read error").isNotDisplayed()
            onNodeWithContentDescription(getString(Res.string.open_drawer)).isDisplayed()

            assertFalse(activity.isFinishing)

            pressBack()
            waitForIdle()

            assertTrue(activity.isFinishing)
        }
    }

    @Test
    fun testDoseDeletion() = runBlocking {
        composeTestRule.run {
            doseStorage.insertData()

            assertEquals(9, doseStorage.getAllDoses().first().size)

            waitForIdle()

            onNodeWithText("52.0").performClick()
            waitForIdle()

            onNodeWithText("2.0").isDisplayed()

            onNodeWithTag("doseGroupDetails", useUnmergedTree = true).performTouchInput {
                longClick()
            }
            waitForIdle()

            onNodeWithText(getString(Res.string.delete_selected))
                .assertIsDisplayed()
                .assertIsNotEnabled()

            onNodeWithTag("doseCheck8").assertIsDisplayed()
            onNodeWithTag("doseCheck9").assertIsDisplayed()
                .performClick()
            waitForIdle()

            onNodeWithText(getString(Res.string.delete_selected))
                .assertIsDisplayed()
                .assertIsEnabled()
                .performClick()
            waitForIdle()

            onNodeWithText(getString(Res.string.delete_dose_warning))
                .assertIsDisplayed()

            onNodeWithText(getString(Res.string.ok))
                .performClick()
            waitForIdle()

            onNodeWithText(getString(Res.string.delete_dose_warning))
                .assertIsNotDisplayed()

            onNodeWithTag("doseCheck8").assertExists()
            onNodeWithTag("doseCheck9").assertDoesNotExist()

            onNodeWithText(getString(Res.string.delete_selected))
                .assertIsDisplayed()
                .assertIsNotEnabled()

            onNodeWithText("52.0").assertDoesNotExist()

            assertEquals(8, doseStorage.getAllDoses().first().size)
        }
    }
}

fun <R: TestRule, A: ComponentActivity> AndroidComposeTestRule<R, A>.pressBack() {
    activity.runOnUiThread {
        activity.onBackPressedDispatcher.onBackPressed()
    }
}
