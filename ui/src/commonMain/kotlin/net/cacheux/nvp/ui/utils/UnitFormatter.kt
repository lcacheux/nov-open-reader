package net.cacheux.nvp.ui.utils

import net.cacheux.nvp.model.InsulinUnit

fun InsulinUnit?.formatUnit(): String {
    return this?.let {
        String.format("%.1f", this.toFloat())
    } ?: "0.0"
}