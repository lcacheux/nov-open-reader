package net.cacheux.nvp.app

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
