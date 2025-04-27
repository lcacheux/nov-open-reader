package net.cacheux.nvp.app

import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toPixelMap
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import net.cacheux.nvp.ui.hexToColor
import net.cacheux.nvp.ui.ui.generated.resources.Res
import net.cacheux.nvp.ui.ui.generated.resources.ok
import net.cacheux.nvp.ui.ui.generated.resources.pen_color
import net.cacheux.nvp.ui.ui.generated.resources.pen_name
import net.cacheux.nvp.ui.ui.generated.resources.pen_settings
import net.cacheux.nvplib.storage.DoseStorage
import org.jetbrains.compose.resources.getString
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@UninstallModules(NvpModule::class)
@RunWith(AndroidJUnit4::class)
class PenSettingsTest {

    companion object {
        val BACKGROUND = "fffbfe".hexToColor()
    }

    @Inject
    lateinit var doseStorage: DoseStorage

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Before
    fun init() {
        hiltRule.inject()
        doseStorage.insertData()
    }

    @Test
    fun configurePens() = runBlocking {
        composeTestRule.run {
            waitForIdle()

            openDrawer()

            assertHaveTexts("ABCD1234", "ABCD5678")

            onNodeWithText(getString(Res.string.pen_settings)).performClick()
            waitForIdle()

            onAllNodesWithText(getString(Res.string.pen_name)).assertCountEquals(2)
            onAllNodesWithText(getString(Res.string.pen_color)).assertCountEquals(2)

            setPenName("ABCD1234", "First pen")
            setPenName("ABCD5678", "Second pen")

            setPenColor("ABCD1234", "ff0000")
            setPenColor("ABCD5678", "0000ff")

            closeSettings()

            onNodeWithText("52.0").testColor("0000ff")
            onNodeWithText("46.0").testColor("ff0000")

            openDrawer()

            assertHaveTexts("First pen", "Second pen")
        }
    }

    private suspend fun ComposeTestRule.setPenName(serial: String, name: String) {
        onNodeWithTag("penNameProp$serial").performClick()
        waitForIdle()

        onNodeWithTag("prefTextInput").run {
            assertTextEquals("")
            performTextInput(name)
        }
        waitForIdle()
        onNodeWithText(getString(Res.string.ok)).performClick()
        waitForIdle()
    }

    private fun SemanticsNodeInteraction.testColor(color: String) {
        val expectedColor = color.hexToColor().copy(alpha = 0.5f).compositeOver(BACKGROUND)

        val map = captureToImage().toPixelMap()
        assertEquals(expectedColor, map[0, 0])
        assertEquals(expectedColor, map[1, 0])
        assertEquals(expectedColor, map[0, 1])
        assertEquals(expectedColor, map[1, 1])
    }

    private suspend fun ComposeTestRule.setPenColor(serial: String, color: String) {
        onNodeWithTag("penColorProp$serial").performClick()
        waitForIdle()

        onNodeWithTag("colorPrefValue").performTextReplacement(color)
        waitForIdle()
        onNodeWithText(getString(Res.string.ok)).performClick()
        waitForIdle()
    }
}
