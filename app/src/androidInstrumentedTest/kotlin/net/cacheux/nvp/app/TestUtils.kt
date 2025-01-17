package net.cacheux.nvp.app

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher

fun isSwitch() = SemanticsMatcher("Is switch") {
    it.config.getOrNull(SemanticsProperties.Role) == Role.Switch
}
