package net.cacheux.nvp.app

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.runBlocking
import net.cacheux.nvp.app.repository.PenInfoRepository
import net.cacheux.nvp.ui.ui.generated.resources.Res
import net.cacheux.nvp.ui.ui.generated.resources.open_drawer
import net.cacheux.nvp.ui.ui.generated.resources.reading_pen
import org.jetbrains.compose.resources.getString
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
}

fun <R: TestRule, A: ComponentActivity> AndroidComposeTestRule<R, A>.pressBack() {
    activity.runOnUiThread {
        activity.onBackPressedDispatcher.onBackPressed()
    }
}
