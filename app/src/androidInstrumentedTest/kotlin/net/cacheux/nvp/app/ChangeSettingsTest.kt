package net.cacheux.nvp.app

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import net.cacheux.nvp.model.Dose
import net.cacheux.nvp.model.PenInfos
import net.cacheux.nvp.ui.testDateTime
import net.cacheux.nvp.ui.ui.generated.resources.Res
import net.cacheux.nvp.ui.ui.generated.resources.auto_ignore
import net.cacheux.nvp.ui.ui.generated.resources.auto_ignore_details
import net.cacheux.nvp.ui.ui.generated.resources.auto_ignore_value
import net.cacheux.nvp.ui.ui.generated.resources.auto_ignore_value_suffix
import net.cacheux.nvp.ui.ui.generated.resources.back_button
import net.cacheux.nvp.ui.ui.generated.resources.cancel
import net.cacheux.nvp.ui.ui.generated.resources.group_delay
import net.cacheux.nvp.ui.ui.generated.resources.group_delay_suffix
import net.cacheux.nvp.ui.ui.generated.resources.ok
import net.cacheux.nvp.ui.ui.generated.resources.open_drawer
import net.cacheux.nvp.ui.ui.generated.resources.settings
import net.cacheux.nvplib.storage.DoseStorage
import org.jetbrains.compose.resources.getString
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@UninstallModules(NvpModule::class)
@RunWith(AndroidJUnit4::class)
class ChangeSettingsTest {

    @Inject
    lateinit var doseStorage: DoseStorage

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()

        val pen1 = PenInfos(
            serial = "ABCD1234",
            model = "NovoPen 6"
        )

        val pen2 = PenInfos(
            serial = "ABCD568",
            model = "NovoPen 6"
        )

        runBlocking {
            doseStorage.addDose(testDose(1, 12, 0, 0, 20), pen1)
            doseStorage.addDose(testDose(1, 12, 0, 10, 40), pen1)
            doseStorage.addDose(testDose(1, 12, 0, 20, 160), pen1)
            doseStorage.addDose(testDose(1, 12, 0, 40, 160), pen1)

            doseStorage.addDose(testDose(1, 12, 10, 0, 20), pen1)
            doseStorage.addDose(testDose(1, 12, 10, 10, 60), pen1)
            doseStorage.addDose(testDose(1, 12, 10, 20, 400), pen1)

            doseStorage.addDose(testDose(1, 12, 10, 30, 20), pen2)
            doseStorage.addDose(testDose(1, 12, 10, 40, 520), pen2)
        }
    }

    @Test
    fun changeSettings() = runBlocking {
        composeTestRule.run {
            waitForIdle()

            assertHaveTexts("01/02/2024",
                "12:00:40", "12:10:20", "12:10:40",
                "36.0", "46.0", "52.0"
            )

            openSettings()

            onNodeWithText(getString(Res.string.group_delay)).assertIsDisplayed()
            onNodeWithText("60" + getString(Res.string.group_delay_suffix)).assertIsDisplayed()
            onNodeWithText(getString(Res.string.auto_ignore)).assertIsDisplayed()
            onNodeWithText(getString(Res.string.auto_ignore_details)).assertIsDisplayed()
            onNodeWithText("2" + getString(Res.string.auto_ignore_value_suffix)).assertIsDisplayed()

            onNodeWithTag("autoIgnoreValuePref").performClick()
            waitForIdle()

            // Check that cancel button doesn't change value
            onNodeWithText(getString(Res.string.cancel)).performClick()
            waitForIdle()
            onNodeWithText("2" + getString(Res.string.auto_ignore_value_suffix)).assertIsDisplayed()

            // Change auto ignore value to 4
            onNodeWithTag("autoIgnoreValuePref").performClick()
            waitForIdle()
            onNodeWithText("2").performTextReplacement("4")
            waitForIdle()
            onNodeWithText(getString(Res.string.ok)).performClick()
            waitForIdle()
            onNodeWithText("4" + getString(Res.string.auto_ignore_value_suffix)).assertIsDisplayed()

            closeSettings()

            assertHaveTexts("32.0", "46.0", "52.0")

            openSettings()

            // Disable auto ignore
            onNodeWithTag("autoIgnoreSwitch").performClick()
            waitForIdle()

            onNodeWithText(getString(Res.string.auto_ignore_value)).assertDoesNotExist()

            closeSettings()

            assertHaveTexts("38.0", "48.0", "54.0")

            openSettings()

            // Re-enable auto ignore
            onNodeWithTag("autoIgnoreSwitch").performClick()
            waitForIdle()
            onNodeWithTag("autoIgnoreValuePref").performClick()
            waitForIdle()
            onNodeWithText("4").performTextReplacement("2")
            waitForIdle()
            onNodeWithText(getString(Res.string.ok)).performClick()
            waitForIdle()

            // Change group delay to 15 minutes
            onNodeWithTag("groupDelayPref").performClick()
            waitForIdle()
            onNodeWithText("60").performTextReplacement("900")
            waitForIdle()
            onNodeWithText(getString(Res.string.ok)).performClick()
            waitForIdle()
            onNodeWithText("900" + getString(Res.string.group_delay_suffix)).assertIsDisplayed()

            closeSettings()

            assertHaveTexts("84.0", "52.0")

            openSettings()

            // Disable grouping
            onNodeWithTag("groupDosesSwitch").performClick()
            waitForIdle()

            closeSettings()

            assertHaveTexts("52.0", "40.0", "6.0", "4.0")

            onAllNodesWithText("16.0").assertCountEquals(2)
            // Ignore since some could not be visible
            //onAllNodesWithText("2.0").assertCountEquals(3)
        }
        Unit
    }

    private fun ComposeTestRule.assertHaveTexts(vararg values: String) {
        values.forEach {
            onNodeWithText(it).assertExists()
        }
    }

    private suspend fun ComposeTestRule.openSettings() {
        onNodeWithContentDescription(getString(Res.string.open_drawer)).performClick()
        waitForIdle()

        onNodeWithText(getString(Res.string.settings)).performClick()
        waitForIdle()
    }

    private suspend fun ComposeTestRule.closeSettings() {
        onNodeWithContentDescription(getString(Res.string.back_button)).performClick()
        waitForIdle()
    }

    private fun testDose(day: Int, hour: Int, minute: Int, second: Int, value: Int) = Dose(
        time = testDateTime(hour, minute, second, date = day),
        value = value
    )
}
