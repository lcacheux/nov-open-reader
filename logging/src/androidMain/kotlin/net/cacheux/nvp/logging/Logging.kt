package net.cacheux.nvp.logging

import android.util.Log

inline fun logDebug(message: () -> String) {
    if (BuildConfig.DEBUG)
        Log.d("NvpLib", message())
}
