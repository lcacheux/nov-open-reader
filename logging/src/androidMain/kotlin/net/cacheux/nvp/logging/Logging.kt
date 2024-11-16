package net.cacheux.nvp.logging

import android.util.Log

fun logDebug(message: () -> String) {
    Log.d("NvpLib", message())
}
