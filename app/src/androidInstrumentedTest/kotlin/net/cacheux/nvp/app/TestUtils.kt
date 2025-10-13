package net.cacheux.nvp.app

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import net.cacheux.nvp.ui.ui.generated.resources.Res
import net.cacheux.nvp.ui.ui.generated.resources.back_button
import net.cacheux.nvp.ui.ui.generated.resources.open_drawer
import org.jetbrains.compose.resources.getString
import org.junit.Assert.assertTrue
import kotlin.math.abs

fun isSwitch() = SemanticsMatcher("Is switch") {
    it.config.getOrNull(SemanticsProperties.Role) == Role.Switch
}

fun ComposeTestRule.assertHaveTexts(vararg values: String) {
    values.forEach {
        onNodeWithText(it).assertExists()
    }
}

suspend fun ComposeTestRule.openDrawer() {
    onNodeWithContentDescription(getString(Res.string.open_drawer)).performClick()
    waitForIdle()
}

suspend fun ComposeTestRule.closeSettings() {
    onNodeWithContentDescription(getString(Res.string.back_button)).performClick()
    waitForIdle()
}

fun assertColorClose(expected: Color, actual: Color, tolerance: Int = 2) {
    fun close(a: Float, b: Float) = abs(a - b) * 255 <= tolerance
    assertTrue(
        "Expected color $expected but got $actual",
        close(expected.alpha, actual.alpha) &&
                close(expected.red, actual.red) &&
                close(expected.green, actual.green) &&
                close(expected.blue, actual.blue)
    )
}
